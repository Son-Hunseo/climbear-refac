import cv2
import torch
from typing import List, Dict
from dto import hold_dto
from dto import response_dto
from sklearn.cluster import KMeans
import requests
import numpy as np
import pathlib
import platform
from segment_anything import sam_model_registry, SamPredictor
from skimage.color import rgb2lab, deltaE_ciede2000
from ultralytics import YOLO


# linux에서 에러 해결하기 위한 코드
if platform.system() != 'Windows':
    pathlib.WindowsPath = pathlib.PosixPath

# YOLO 모델 로드 (최초 한 번만 로드하도록 전역에서 수행)
_yolo_model = YOLO("weights/yolov11l_batch16_epoch100_mAP50_90_mAP50-95_80.pt")

# ③ SAM (폴리곤 추출)
SAM_CKPT   = "sam/sam_vit_b_01ec64.pth"
SAM_TYPE   = "vit_b"
_device    = "cuda" if torch.cuda.is_available() else "cpu"

_sam       = sam_model_registry[SAM_TYPE](checkpoint=SAM_CKPT)
_sam.to(device=_device)
_sam_pred  = SamPredictor(_sam)                   # 이미지마다 set_image()만 호출

# 어짜피 yolov5가 동기로 짜여있어서 async def 의미 없음
# 이미지 경로를 입력 받아 YOLOv5로 추론을 수행하고 결과 객체를 반환
def run_inference(image_bgr: np.ndarray):
    """
    Ultralytics YOLOv11 (BGR numpy 이미지) → results(list[Results])
    """
    return _yolo_model.predict(
        source=image_bgr,     # numpy 배열 직접 전달
        imgsz=1024,
        conf=0.25,
        verbose=False
    )

# run_inference가 def 라서 async def 의미 없음
def extract_hold_coordinates(results, image_bgr) -> List[hold_dto.Hold]:
    if not results:
        return []

    image_rgb = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2RGB)
    _sam_pred.set_image(image_rgb)

    holds: List[hold_dto.Hold] = []
    res = results[0]                      # 단일 이미지

    for idx, box in enumerate(res.boxes): # new API
        # ── (1) YOLO box
        x1, y1, x2, y2 = map(int, box.xyxy[0].tolist())
        yolo_box = np.array([x1, y1, x2, y2])

        # ── (2) SAM mask
        masks, scores, _ = _sam_pred.predict(
            box=yolo_box[None, :],
            multimask_output=True
        )
        if scores.size == 0:
            continue

        best_mask = masks[np.argmax(scores)]

        # ── (3) 외곽선 → 폴리곤
        contours, _ = cv2.findContours(
            best_mask.astype(np.uint8),
            cv2.RETR_EXTERNAL,
            cv2.CHAIN_APPROX_SIMPLE
        )
        if not contours:
            continue
        contour = max(contours, key=cv2.contourArea)

        poly = [hold_dto.Coordinate(x=int(pt[0][0]), y=int(pt[0][1]))
                for pt in contour]

        # 평균 좌표
        avg_x = int(sum(p.x for p in poly) / len(poly))
        avg_y = int(sum(p.y for p in poly) / len(poly))

        holds.append(
            hold_dto.Hold(
                hold_id=idx,
                average=hold_dto.Coordinate(x=avg_x, y=avg_y),
                coordinates=poly
            )
        )
    return holds

def calculate_hsv_similarity(hsv1, hsv2):
    h1, s1, v1 = hsv1
    h2, s2, v2 = hsv2
    h_diff = min(abs(h1 - h2), 180 - abs(h1 - h2)) / 90.0
    s_diff = abs(s1 - s2) / 255.0
    v_diff = abs(v1 - v2) / 255.0
    weighted_diff = (h_diff * 0.6) + (s_diff * 0.3) + (v_diff * 0.1)
    return 100 * (1 - weighted_diff)

def extract_dominant_hsv(img_hsv, box_coords):
    x1 = min(p['x'] for p in box_coords)
    y1 = min(p['y'] for p in box_coords)
    x2 = max(p['x'] for p in box_coords)
    y2 = max(p['y'] for p in box_coords)
    roi = img_hsv[y1:y2, x1:x2]
    if roi.size == 0:
        return None
    pixels = roi.reshape(-1, 3)
    kmeans = KMeans(n_clusters=2, n_init=10).fit(pixels)
    dominant = kmeans.cluster_centers_[np.argmax(np.bincount(kmeans.labels_))]
    return dominant.astype(int)

def classify_holds_by_color(
    image_url: str,
    selected_hold_id_list: List[int],
    holds: List[Dict],
    delta_e_threshold: float = 20.0,
    n_clusters: int = 2
) -> response_dto.classify_response:
    # ───────────────────────────────
    # 1. 이미지 로드 (URL → RGB)
    # ───────────────────────────────
    resp = requests.get(image_url)
    img_arr = np.asarray(bytearray(resp.content), dtype=np.uint8)
    img_bgr = cv2.imdecode(img_arr, cv2.IMREAD_COLOR)
    if img_bgr is None:
        raise ValueError("이미지를 로드할 수 없습니다.")
    img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
    img_h, img_w = img_rgb.shape[:2]          # ← 이름 충돌 방지

    # ───────────────────────────────
    # 2. 보조 함수: dominant Lab 추출
    # ───────────────────────────────
    def extract_dominant_lab(polygon_xy: List[Dict]) -> np.ndarray | None:
        """폴리곤 ROI의 dominant Lab 색 벡터 반환 (shape=(3,)), 없으면 None"""
        mask = np.zeros((img_h, img_w), np.uint8)
        pts = np.array([(int(p["x"]), int(p["y"])) for p in polygon_xy], np.int32)
        if len(pts) < 3:
            return None
        cv2.fillPoly(mask, [pts], 255)

        roi_rgb = img_rgb[mask == 255]
        if roi_rgb.size == 0:
            return None

        roi_lab = rgb2lab(roi_rgb.astype(np.float32) / 255.0)
        km = KMeans(n_clusters=n_clusters, n_init=10, random_state=0).fit(roi_lab)
        dominant_lab = km.cluster_centers_[np.argmax(np.bincount(km.labels_))]
        return dominant_lab  # shape (3,)

    # ───────────────────────────────
    # 3. 기준색(Lab) 계산
    # ───────────────────────────────
    ref_labs = []
    for hid in selected_hold_id_list:
        hold_info = next((h for h in holds if h["hold_id"] == hid), None)
        if hold_info:
            lab = extract_dominant_lab(hold_info["coordinates"])
            if lab is not None:
                ref_labs.append(lab)

    if not ref_labs:
        raise ValueError("selected_hold_id_list에 해당되는 홀드가 없습니다.")

    ref_lab = np.mean(ref_labs, axis=0)  # shape (3,)

    # ───────────────────────────────
    # 4. 전체 홀드 ΔE → 분류
    # ───────────────────────────────
    selected, not_selected = [], []

    for hold in holds:                              # ← 변수 이름 충돌 없음
        lab = extract_dominant_lab(hold["coordinates"])
        if lab is None:
            continue
        delta_e = float(deltaE_ciede2000(ref_lab[np.newaxis],
                                         lab[np.newaxis])[0])
        if delta_e <= delta_e_threshold:
            selected.append(hold["hold_id"])
        else:
            not_selected.append(hold["hold_id"])

    return response_dto.classify_response(selected=selected, not_selected=not_selected)
