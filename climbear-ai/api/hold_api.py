from fastapi import APIRouter
from typing import List
from dto import hold_dto, request_dto, response_dto
from services import hold_service
import cv2
import requests
import numpy as np

router = APIRouter(prefix="/ai/v1/hold", tags=["hold"])

# S3에 업로드된 사진 파일을 통해 홀드 위치를 추출
# 내부에서 호출하는 함수가 동기이므로 여기서 async해도 의미가 없어서 def
@router.post("", response_model=List[hold_dto.Hold])
def get_hold_from_picture(request: request_dto.picture_request):
    resp = requests.get(request.picture_url, timeout=60)
    img_arr = np.asarray(bytearray(resp.content), dtype=np.uint8)
    image_bgr = cv2.imdecode(img_arr, cv2.IMREAD_COLOR)

    yolo_results = hold_service.run_inference(image_bgr)
    return hold_service.extract_hold_coordinates(yolo_results, image_bgr)

@router.post("/classify", response_model=response_dto.classify_response)
def classify_holds(req: request_dto.HoldRequest):
    return hold_service.classify_holds_by_color(
        image_url=req.image_url,
        selected_hold_id_list=req.selected_hold_id_list,
        holds=[h.dict() for h in req.holds]
    )