package com.climb.api.solution.service;

import com.climb.api.solution.domain.type.HoldGeometryDTO;
import com.climb.api.solution.domain.dto.request.FailureRequestDTO;
import com.climb.api.solution.domain.dto.request.NonMemberFailureRequestDTO;
import com.climb.api.solution.domain.dto.request.NonMemberProblemRequestDTO;
import com.climb.api.solution.domain.dto.response.ProblemSolutionResponseDTO;
import com.climb.api.problem.domain.type.Coordinate;
import com.climb.api.problem.domain.type.Hold;
import com.climb.api.problem.domain.entity.ProblemData;
import com.climb.api.problem.repository.ProblemDataRepository;
import com.climb.api.user.domain.entity.User;
import com.climb.api.user.repository.UserRepository;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionService {

    private final ProblemDataRepository problemDataRepository;
    private final UserRepository userRepository;

    /** 더클라임 그리드 간격 25cm */
    private final static int realHeight = 25;  // cm 환산

    /** 발 휴리스틱 가중치 */
    private static final double FOOT_HEURISTIC_WEIGHT = 0.5;

    /** 발 휴리스틱 전환점 */
    private static final double FOOT_HEIGHT_THRESHOLD_CM = 200;

    private static final List<String> LIMB_SEQUENCE = List.of("leftFoot","rightFoot","leftHand", "rightHand");

    /** 클라이밍 솔루션 생성 - 회원 */
    public ProblemSolutionResponseDTO getProblemSolution(Integer userId, int problemId) {
        // 클라이밍 문제 데이터 조회
        ProblemData problem = problemDataRepository.findById(problemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

         // 사용자 정보 조회 (사용자의 키 정보가 필요한 경우)
        User userInfo = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User user = User.builder()
                .height(userInfo.getHeight() == null ? 165.0 : userInfo.getHeight())
                .armSpan(userInfo.getArmSpan() == null ? 155.0 : userInfo.getArmSpan())
                .build();

        int pixelGrid = problem.getPixelGrid();
        double perPixel = (double) realHeight /pixelGrid;

        // 홀드들에 대한 헐/AABB/거리 캐싱
        HoldGeometryDTO geom = buildHoldGeometryDTO(problem.getSelected());

        // 시작 손/발 위치 설정
        Map<String, Integer> initialPosition = setInitialPosition(problem.getStartHold(), problem.getSelected(), perPixel, geom);

        // 경로 탐색
        List<Map<String, Integer>> solutionPath = findOptimalPath(problem, user, initialPosition, geom, perPixel);

        return buildResponse(solutionPath, initialPosition);
    }

    /** 실패 지점부터 클라이밍 솔루션 생성 - 회원 */
    public ProblemSolutionResponseDTO getFailureSolution(Integer userId, int problemId, FailureRequestDTO failureRequestDTO) {
        // 클라이밍 문제 데이터 조회
        ProblemData problem = problemDataRepository.findById(problemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // 사용자 정보 조회 (사용자의 키 정보가 필요한 경우)
        User userInfo = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User user = User.builder()
                .height(userInfo.getHeight() == null ? 165.0 : userInfo.getHeight())
                .armSpan(userInfo.getArmSpan() == null ? 155.0 : userInfo.getArmSpan())
                .build();

        double perPixel = (double) realHeight /problem.getPixelGrid();

        HoldGeometryDTO geom = buildHoldGeometryDTO(problem.getSelected());

        // 실패 지점부터 시작점 설정
        Map<String, Integer> failurePosition = new HashMap<>();
        failurePosition.put("leftHand", failureRequestDTO.leftHand());
        failurePosition.put("rightHand", failureRequestDTO.rightHand());
        failurePosition.put("leftFoot", failureRequestDTO.leftFoot());
        failurePosition.put("rightFoot", failureRequestDTO.rightFoot());

        // 경로 탐색
        List<Map<String, Integer>> solutionPath = findOptimalPath(problem, user, failurePosition, geom, perPixel);

        // 응답 생성
        return buildResponse(solutionPath, failurePosition);
    }

    /** 클라이밍 솔루션 생성 - 비회원 */
    public ProblemSolutionResponseDTO getNonMemberProblemSolution(NonMemberProblemRequestDTO requestDTO) {
        ProblemData problem = problemDataRepository.findById(requestDTO.problemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = User.builder()
                .height(requestDTO.height() == null ? 165.0 : requestDTO.height())
                .armSpan(requestDTO.armSpan() == null ? 155.0 : requestDTO.armSpan())
                .build();

        int pixelGrid = problem.getPixelGrid();
        double perPixel = (double) realHeight /pixelGrid;

        HoldGeometryDTO geom = buildHoldGeometryDTO(problem.getSelected());

        Map<String, Integer> initialPosition = setInitialPosition(problem.getStartHold(), problem.getSelected(), perPixel, geom);

        List<Map<String, Integer>> solutionPath = findOptimalPath(problem, user, initialPosition, geom, perPixel);

        return buildResponse(solutionPath, initialPosition);
    }

    /** 실패 지점부터 클라이밍 솔루션 생성 - 비회원 */
    public ProblemSolutionResponseDTO getNonMemberFailureSolution(NonMemberFailureRequestDTO requestDTO) {
        ProblemData problem = problemDataRepository.findById(requestDTO.problemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        User user = User.builder()
                .height(requestDTO.height() == null ? 165.0 : requestDTO.height())
                .armSpan(requestDTO.armSpan() == null ? 155.0 : requestDTO.armSpan())
                .build();

        double perPixel = (double) realHeight /problem.getPixelGrid();

        HoldGeometryDTO geom = buildHoldGeometryDTO(problem.getSelected());

        Map<String, Integer> failurePosition = new HashMap<>();
        failurePosition.put("leftHand", requestDTO.leftHand());
        failurePosition.put("rightHand", requestDTO.rightHand());
        failurePosition.put("leftFoot", requestDTO.leftFoot());
        failurePosition.put("rightFoot", requestDTO.rightFoot());

        List<Map<String, Integer>> solutionPath = findOptimalPath(problem, user, failurePosition, geom, perPixel);

        return buildResponse(solutionPath, failurePosition);
    }

    /** 홀드들에 대한 헐/AABB/거리 캐싱 */
    private HoldGeometryDTO buildHoldGeometryDTO(List<Hold> holds) {
        Map<Integer, List<Point>> hullMap = new HashMap<>();
        Map<Integer, AABB> aabbMap = new HashMap<>();
        Map<HoldPair, Double> cachedDist = new HashMap<>();

        // 각 홀드에 대해 컨벡스 헐 + AABB 계산
        for (Hold h : holds) {
            List<Point> pts = h.getCoordinates().stream()
                    .map(c -> new Point(c.getX(), c.getY()))
                    .collect(Collectors.toList());
            List<Point> hull = ConvexHull.compute(pts);  // Graham scan 등
            hullMap.put(h.getHoldId(), hull);

            aabbMap.put(h.getHoldId(), AABB.fromPoints(hull));
        }

        // 쌍별 헐 간 최소 거리 미리 계산
        for (int holdIndex = 0; holdIndex < holds.size(); holdIndex++) {
            for (int anotherHoldIndex = holdIndex +1; anotherHoldIndex < holds.size(); anotherHoldIndex++) {
                int firstHoldId = holds.get(holdIndex).getHoldId();
                int secondHoldId = holds.get(anotherHoldIndex).getHoldId();
                double d = ConvexHull.minDistanceBetween(hullMap.get(firstHoldId), hullMap.get(secondHoldId));
                cachedDist.put(new HoldPair(firstHoldId, secondHoldId), d);
                cachedDist.put(new HoldPair(secondHoldId, firstHoldId), d);
            }
        }
        return new HoldGeometryDTO(hullMap, aabbMap, cachedDist);
    }

    /** 손/발 초기 위치 설정 */
    private Map<String, Integer> setInitialPosition(List<Integer> startHolds, List<Hold> allHolds, double perPixel,
                                                    HoldGeometryDTO geom) {
        Map<String, Integer> position = new HashMap<>();

        // 시작점이 1개일 경우 - 양손 모두 같은 홀드
        if (startHolds.size() == 1) {
            position.put("rightHand", startHolds.get(0));
            position.put("leftHand", startHolds.get(0));
        }
        // 시작점이 2개일 경우 - 왼손은 왼쪽, 오른손은 오른쪽 홀드
        else if (startHolds.size() == 2) {
            position.put("leftHand", startHolds.get(0));
            position.put("rightHand", startHolds.get(1));
        }

        // 손 홀드보다 아래에 있는 홀드 중 하나만 있으면, 양발 모두 그 홀드에 위치
        double maxHandY = Arrays.asList("leftHand", "rightHand").stream()
                .mapToDouble(key -> {
                    int id = position.get(key);
                    AABB aabb = geom.getAabbMap().get(id);
                    return (aabb.getMinY() + aabb.getMaxY()) / 2;
                })
                .max().orElse(Double.NEGATIVE_INFINITY);
        List<Integer> below = allHolds.stream()
                .filter(h -> {
                    AABB aabb = geom.getAabbMap().get(h.getHoldId());
                    double cy = (aabb.getMinY() + aabb.getMaxY()) / 2;
                    return cy > maxHandY;
                })
                .map(Hold::getHoldId)
                .toList();
        if (below.size() == 1) {
            position.put("leftFoot", below.get(0));
            position.put("rightFoot", below.get(0));
            return position;
        }

        // 가장 낮은 홀드 4개를 뽑고,
        // 그중 첫 번째(가장 낮은 홀드)를 기준으로 x축 거리 차이가 30cm 이상인 홀드를 찾은 뒤,
        // 그 후보들 중 가장 낮은 홀드를 두 번째 발 후보로 삼고,
        // x 좌표가 작은 쪽을 왼발, 큰 쪽을 오른발에 배치한다.
        double lowerThresholdCm = 20.0;
        double upperThresholdCm = 60.0;

        List<Hold> lowestHolds = allHolds.stream()
                .sorted(Comparator.comparingDouble(
                        (Hold h) -> findCenter(h.getCoordinates()).getY()
                ).reversed())
                .limit(4)
                .toList();

        if (!lowestHolds.isEmpty()) {
            // 2) 기준 홀드
            Hold base = lowestHolds.get(0);
            double baseX = findCenter(base.getCoordinates()).getX();

            // 3) x축 차이(실거리) 30cm 이상인 후보들 중에서 가장 낮은 홀드
            Optional<Hold> optSecond = lowestHolds.stream()
                    .skip(1)
                    .filter(h -> {
                        double xDiffPx = Math.abs(
                                findCenter(h.getCoordinates()).getX() - baseX
                        );
                        double xDiffCm = xDiffPx * perPixel;
                        return xDiffCm >= lowerThresholdCm && xDiffCm <= upperThresholdCm;
                    })
                    .min(Comparator.comparingDouble(h ->
                            findCenter(h.getCoordinates()).getY()
                    ));

            if (optSecond.isPresent()) {
                Hold second = optSecond.get();
                double secondX = findCenter(second.getCoordinates()).getX();

                // 왼발/오른발 배치
                if (baseX < secondX) {
                    position.put("leftFoot",  base.getHoldId());
                    position.put("rightFoot", second.getHoldId());
                } else {
                    position.put("leftFoot",  second.getHoldId());
                    position.put("rightFoot", base.getHoldId());
                }
            } else {
                // (Fallback) 조건에 맞는 홀드가 없으면, x축 기준으로 두 개 뽑기
                List<Hold> twoByX = lowestHolds.stream()
                        .sorted(Comparator.comparingDouble(h ->
                                findCenter(h.getCoordinates()).getX()
                        ))
                        .limit(2)
                        .toList();
                position.put("leftFoot",  twoByX.get(0).getHoldId());
                position.put("rightFoot", twoByX.get(1).getHoldId());
            }
        } else {
            // 홀드가 하나도 없으면 null
            position.put("leftFoot",  null);
            position.put("rightFoot", null);
        }
        return position;
    }

    // ========== A* 알고리즘 헬퍼 클래스 ==========
    private static class AStarNode {
        Map<String, Integer> position;
        double g; // 시작점에서 현재 노드까지의 비용
        double h; // 현재 노드에서 목표까지의 추정 비용
        double f; // g + h
        AStarNode parent;
        Map<String, Integer> prevPosition;
        Set<String> movedLimbs;

        AStarNode(Map<String, Integer> position,
                  Map<String, Integer> prevPosition,
                  Set<String> movedLimbs,
                  double g,
                  double h) {
            this.position = position;
            this.prevPosition = prevPosition;
            this.movedLimbs = movedLimbs;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }

    // ========== 솔루션 생성 로직 ==========
    /** A* 알고리즘 */
    private List<Map<String, Integer>> findOptimalPath(ProblemData problem, User user, Map<String, Integer> currentPosition, HoldGeometryDTO geom, double perPixel) {

        List<Map<String, Integer>> solutionPath;
        User currentUser = user;
        for (int attempt = 0; attempt < 3; attempt++) {
            log.info("경로 탐색 시도 {})", attempt + 1);

            // 경로 탐색
            solutionPath = findPathUsingAStar(problem, currentUser, currentPosition, geom, perPixel);

            // 솔루션을 찾으면 즉시 반환
            if (!solutionPath.isEmpty()) {
                log.info("시도 {}에서 솔루션 발견.", attempt + 1);
                return solutionPath;
            }

            // 솔루션을 찾지 못했고, 마지막 시도가 아니면 비율 조정 후 재도전
            log.info("솔루션 미발견. 비율 조정 후 재도전");
            currentUser = User.builder()
                    .height(currentUser.getHeight() + 10.0)
                    .armSpan(currentUser.getArmSpan()+ 10.0)
                    .build();

        }

        // 모든 시도 후에도 솔루션을 찾지 못하면 경고 로그를 남기고 빈 리스트 반환
        log.warn("문제에 대해 3번의 시도 후에도 솔루션을 찾지 못했습니다 ");
        return new ArrayList<>();
    }

    /** 홀드 중심 X(px) 구하기 */
    private double centerXof(Hold h) {
        if (h == null) return 0;
        return findCenter(h.getCoordinates()).getX();
    }

    /** A* 알고리즘을 사용한 경로 탐색 */
    private List<Map<String, Integer>> findPathUsingAStar(
            ProblemData problem,
            User user,
            Map<String, Integer> startPosition,
            HoldGeometryDTO geom,
            double perPixel) {
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Set<String> closedSet = new HashSet<>();
        Map<String, AStarNode> allNodes = new HashMap<>();

        List<String> limbSequence = determineLimbSequence(problem, geom);

        AStarNode startNode = new AStarNode(
                startPosition,
                startPosition,
                new LinkedHashSet<>(),
                0,
                calculateHeuristic(startPosition, problem.getEndHold(), problem.getSelected(), geom, perPixel)
        );
        String startKey = positionKey(startPosition) + "|" + "";
        openSet.add(startNode);
        allNodes.put(startKey, startNode);

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            String currKey = positionKey(current.position) + "|" + String.join(",", current.movedLimbs);
            if (closedSet.contains(currKey)) continue;

            if (hasReachedEnd(current.position, problem.getEndHold())) {
                return reconstructPath(current);
            }
            closedSet.add(currKey);

            for (Map<String, Integer> nextPos : generateAllValidMoves(
                    current.prevPosition,
                    current.position,
                    problem.getSelected(),
                    user,
                    geom,
                    current.movedLimbs,
                    perPixel,
                    limbSequence)) {

                // 손과 발이 같은 홀드를 참조하는 경우, 해당 발은 이전 위치로 되돌림
                Map<String, Integer> adjustedPos = new HashMap<>(nextPos);
                List<String> hands = Arrays.asList("leftHand", "rightHand");
                List<String> feet = Arrays.asList("leftFoot", "rightFoot");
                for (String footKey : feet) {
                    for (String handKey : hands) {
                        if (Objects.equals(adjustedPos.get(footKey), adjustedPos.get(handKey))) {
                            // 겹친 발은 이전 위치 유지
                            adjustedPos.put(footKey, current.position.get(footKey));
                        }
                    }
                }

                String movedLimb = detectLastMovedLimb(current.position, adjustedPos);
                // 오른손이 오른발보다 더 오른쪽으로 가려 하면 → 대신 오른발을 움직인다
                if ("rightHand".equals(movedLimb)) {
                    AABB handAabb = geom.getAabbMap().get(adjustedPos.get("rightHand"));
                    double handX = (handAabb.getMinX() + handAabb.getMaxX()) / 2;
                    AABB footAabb = geom.getAabbMap().get(adjustedPos.get("rightFoot"));
                    double footX = (footAabb.getMinX() + footAabb.getMaxX())  / 2;
                    if (handX > footX) {
                        adjustedPos.put("rightHand", current.position.get("rightHand"));
                        movedLimb = "rightFoot";
                    }
                }

                // 왼손이 왼발보다 더 왼쪽으로 가려 하면 → 대신 왼발을 움직인다
                if ("leftHand".equals(movedLimb)) {
                    AABB handAabb = geom.getAabbMap().get(adjustedPos.get("leftHand"));
                    double handX = (handAabb.getMinX() + handAabb.getMaxX()) / 2;
                    AABB footAabb = geom.getAabbMap().get(adjustedPos.get("leftFoot"));
                    double footX = (footAabb.getMinX() + footAabb.getMaxX())  / 2;
                    if (handX < footX) {
                        adjustedPos.put("leftHand", current.position.get("leftHand"));
                        movedLimb = "leftFoot";
                    }
                }

                //이동하려는 발이 반대쪽 발보다 25cm 이상 낮으면, 반대쪽 발 위치로 조정
                if ("leftFoot".equals(movedLimb) || "rightFoot".equals(movedLimb)) {
                    String oppFoot = "leftFoot".equals(movedLimb) ? "rightFoot" : "leftFoot";
                    AABB footAabb = geom.getAabbMap().get(adjustedPos.get(movedLimb));
                    AABB oppAabb = geom.getAabbMap().get(adjustedPos.get(oppFoot));
                    double footCenterY = (footAabb.getMinY() + footAabb.getMaxY()) / 2;
                    double oppCenterY  = (oppAabb.getMinY()  + oppAabb.getMaxY())  / 2;
                    double deltaCm = (oppCenterY - footCenterY) * perPixel;
                    if (deltaCm > 25) {
                        // 발을 반대쪽 발 위치로 고정
                        adjustedPos.put(movedLimb, current.position.get(oppFoot));
                    }
                }

                if ("leftHand".equals(movedLimb) || "rightHand".equals(movedLimb)
                ) {
                    // 후보 손 홀드의 중심 X 좌표
                    Integer targetHoldId = adjustedPos.get("leftHand"); // == rightHand
                    AABB targetAabb    = geom.getAabbMap().get(targetHoldId);
                    double targetX     = (targetAabb.getMinX() + targetAabb.getMaxX()) / 2 * perPixel;

                    // 현재 양 손의 중앙 X 좌표
                    AABB currLhAabb    = geom.getAabbMap().get(current.position.get("leftHand"));
                    AABB currRhAabb    = geom.getAabbMap().get(current.position.get("rightHand"));
                    double currLhX     = (currLhAabb.getMinX() + currLhAabb.getMaxX()) / 2 * perPixel;
                    double currRhX     = (currRhAabb.getMinX() + currRhAabb.getMaxX()) / 2 * perPixel;
                    double currCenterX = (currLhX + currRhX) / 2;

                    // 후보가 오른쪽이면 오른손, 왼쪽이면 왼손을 먼저 움직이도록
                    movedLimb = (targetX > currCenterX) ? "rightHand" : "leftHand";

                }

                if ("leftFoot".equals(movedLimb) || "rightFoot".equals(movedLimb)) {
                    AABB lfAabb2 = geom.getAabbMap().get(adjustedPos.get("leftFoot"));
                    AABB rfAabb2 = geom.getAabbMap().get(adjustedPos.get("rightFoot"));
                    double lfX2 = (lfAabb2.getMinX() + lfAabb2.getMaxX()) / 2;
                    double lfY2 = (lfAabb2.getMinY() + lfAabb2.getMaxY()) / 2;
                    double rfX2 = (rfAabb2.getMinX() + rfAabb2.getMaxX()) / 2;
                    double rfY2 = (rfAabb2.getMinY() + rfAabb2.getMaxY()) / 2;
                    double legSpan = Math.hypot(lfX2 - rfX2, lfY2 - rfY2) * perPixel;
                    if (legSpan > user.getHeight() * 0.6) continue;
                }

                // 발이 손보다 위로 올라가는 움직임을 스킵
                if (isFootAboveHand(nextPos, geom)) {
                    continue;
                }

                // 이동하려는 손과 양발 거리를 재서, 더 먼 발과의 거리가 사용자 키보다 크면 스킵
                if ("leftHand".equals(movedLimb) || "rightHand".equals(movedLimb)) {
                    // 손 중심 좌표
                    AABB handAabb = geom.getAabbMap().get(adjustedPos.get(movedLimb));
                    double handX = (handAabb.getMinX() + handAabb.getMaxX()) / 2;
                    double handY = (handAabb.getMinY() + handAabb.getMaxY()) / 2;
                    // 양발 중심 좌표
                    AABB lfAabb = geom.getAabbMap().get(adjustedPos.get("leftFoot"));
                    AABB rfAabb = geom.getAabbMap().get(adjustedPos.get("rightFoot"));
                    double lfX = (lfAabb.getMinX() + lfAabb.getMaxX()) / 2;
                    double lfY = (lfAabb.getMinY() + lfAabb.getMaxY()) / 2;
                    double rfX = (rfAabb.getMinX() + rfAabb.getMaxX()) / 2;
                    double rfY = (rfAabb.getMinY() + rfAabb.getMaxY()) / 2;
                    // 거리 계산 (cm)
                    double distLf = Math.hypot(handX - lfX, handY - lfY) * perPixel;
                    double distRf = Math.hypot(handX - rfX, handY - rfY) * perPixel;
                    double maxDist = Math.max(distLf, distRf);
                    if (maxDist > user.getHeight() + user.getArmSpan()/3.0) {
                        continue;
                    }
                }

                double bodyAngle = calculateBodyAngle(adjustedPos, geom);
                if (bodyAngle < 55.0 || bodyAngle > 135.0) {
                    continue;
                }

                // 손–발 간 최소 거리 필터: 너무 가까우면 스킵
                {
                    double minHandFootDistCm = 70.0; // 허용할 최소 손–발 거리(cm)
                    List<String> minHands = Arrays.asList("leftHand", "rightHand");
                    List<String> minFeet = Arrays.asList("leftFoot", "rightFoot");
                    boolean tooClose = false;

                    for (String handKey : minHands) {
                        AABB handAabb = geom.getAabbMap().get(adjustedPos.get(handKey));
                        double handX = (handAabb.getMinX() + handAabb.getMaxX()) / 2;
                        double handY = (handAabb.getMinY() + handAabb.getMaxY()) / 2;

                        for (String footKey : minFeet) {
                            AABB footAabb = geom.getAabbMap().get(adjustedPos.get(footKey));
                            double footX = (footAabb.getMinX() + footAabb.getMaxX()) / 2;
                            double footY = (footAabb.getMinY() + footAabb.getMaxY()) / 2;

                            double distCm = Math.hypot(handX - footX, handY - footY) * perPixel;
                            if (distCm < minHandFootDistCm) {
                                tooClose = true;
                                break;
                            }
                        }
                        if (tooClose) break;
                    }

                    if (tooClose) {
                        continue;
                    }
                }
                String key = positionKey(nextPos) + "|" + String.join(",", current.movedLimbs);
                if (closedSet.contains(key)) continue;

                double tentativeG = current.g + calculateMoveCost(current.position, nextPos, problem.getSelected(), geom, perPixel);
                String moved = detectLastMovedLimb(current.position, nextPos);

                Set<String> nextMoved = new LinkedHashSet<>(current.movedLimbs);
                if (moved != null) nextMoved.add(moved);
                if (nextMoved.size() == limbSequence.size()) nextMoved.clear();

                String nextKey = positionKey(nextPos) + "|" + String.join(",", nextMoved);

                AStarNode neighbor = allNodes.computeIfAbsent(nextKey,
                        k -> new AStarNode(nextPos, current.position, nextMoved, Double.POSITIVE_INFINITY, 0)
                );

                if (tentativeG < neighbor.g) {
                    neighbor.prevPosition = current.position;
                    neighbor.movedLimbs = nextMoved;
                    neighbor.g = tentativeG;
                    neighbor.h = calculateHeuristic(nextPos, problem.getEndHold(), problem.getSelected(), geom, perPixel);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // 경로를 찾지 못함
    }

    /** 발이 손 위로 올라갔는지 검사하는 유틸 */
    private boolean isFootAboveHand(Map<String, Integer> position, HoldGeometryDTO geom) {
        List<String> hands = Arrays.asList("leftHand", "rightHand");
        List<String> feet = Arrays.asList("leftFoot", "rightFoot");
        // AABB에서 중심 Y 좌표를 계산하여 비교
        for (String footKey : feet) {
            int footId = position.get(footKey);
            AABB footAabb = geom.getAabbMap().get(footId);
            double footCenterY = (footAabb.getMinY() + footAabb.getMaxY()) / 2;
            for (String handKey : hands) {
                int handId = position.get(handKey);
                AABB handAabb = geom.getAabbMap().get(handId);
                double handCenterY = (handAabb.getMinY() + handAabb.getMaxY()) / 2;
                // Y 값이 작을수록 위쪽이므로, 발이 손보다 위에 있으면 스킵
                if (footCenterY < handCenterY) {
                    return true;
                }
                // 손과 발이 공간적으로 겹치는 경우도 스킵
                if (footAabb.getMinX() < handAabb.getMaxX() && footAabb.getMaxX() > handAabb.getMinX()
                        && footAabb.getMinY() < handAabb.getMaxY() && footAabb.getMaxY() > handAabb.getMinY()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 휴리스틱 계산 : 현재 위치에서 목표까지의 예상 비용 계산 */
    private double calculateHeuristic(Map<String, Integer> position, List<Integer> endHolds, List<Hold> allHolds, HoldGeometryDTO geom, double perPixel) {
        Integer leftHand = position.get("leftHand");
        Integer rightHand = position.get("rightHand");
        double minHandDist = Double.POSITIVE_INFINITY;
        //손 기준 휴리스틱
        for (Integer endHold : endHolds) {
            Hold endHoldObj = findHoldByNumber(allHolds, endHold);

            if (leftHand != null) {
                Hold leftHold = findHoldByNumber(allHolds, leftHand);
                double leftDistance = calculateDistance(leftHold, endHoldObj, geom, perPixel);
                minHandDist = Math.min(minHandDist, leftDistance);
            }

            if (rightHand != null) {
                Hold rightHold = findHoldByNumber(allHolds, rightHand);
                double rightDistance = calculateDistance(rightHold, endHoldObj, geom, perPixel);
                minHandDist = Math.min(minHandDist, rightDistance);
            }
        }
        if (minHandDist == Double.POSITIVE_INFINITY) minHandDist = 0;

        //발 기준 휴리스틱
        double minFootDist = Double.POSITIVE_INFINITY;
        Integer leftFoot  = position.get("leftFoot");
        Integer rightFoot = position.get("rightFoot");

        for (Integer endHold : endHolds) {
            Hold endHoldObj = findHoldByNumber(allHolds, endHold);
            double endY = geom.getAabbMap().get(endHoldObj.getHoldId())
                    .getMinY() + geom.getAabbMap().get(endHoldObj.getHoldId()).getMaxY();
            endY = (endY / 2) * perPixel;

            if (leftFoot != null) {
                AABB lfAabb = geom.getAabbMap().get(leftFoot);
                double lfX = (lfAabb.getMinX() + lfAabb.getMaxX()) / 2 * perPixel;
                double lfY = (lfAabb.getMinY() + lfAabb.getMaxY()) / 2 * perPixel;
                double dy = Math.abs(lfY - endY);

                double dist;
                if (dy <= FOOT_HEIGHT_THRESHOLD_CM) {
                    // 수직 차이가 작으면 가로 거리만
                    AABB endAabb = geom.getAabbMap().get(endHoldObj.getHoldId());
                    double endX = (endAabb.getMinX() + endAabb.getMaxX()) / 2 * perPixel;
                    dist = Math.abs(lfX - endX);
                } else {
                    dist = calculateDistance(
                            findHoldByNumber(allHolds, leftFoot), endHoldObj, geom, perPixel
                    );
                }
                minFootDist = Math.min(minFootDist, dist);
            }

            if (rightFoot != null) {
                AABB rfAabb = geom.getAabbMap().get(rightFoot);
                double rfX = (rfAabb.getMinX() + rfAabb.getMaxX()) / 2 * perPixel;
                double rfY = (rfAabb.getMinY() + rfAabb.getMaxY()) / 2 * perPixel;
                double dy = Math.abs(rfY - endY);

                double dist;
                if (dy <= FOOT_HEIGHT_THRESHOLD_CM) {
                    AABB endAabb = geom.getAabbMap().get(endHoldObj.getHoldId());
                    double endX = (endAabb.getMinX() + endAabb.getMaxX()) / 2 * perPixel;
                    dist = Math.abs(rfX - endX);
                } else {
                    dist = calculateDistance(
                            findHoldByNumber(allHolds, rightFoot), endHoldObj, geom, perPixel
                    );
                }
                minFootDist = Math.min(minFootDist, dist);
            }
        }
        if (minFootDist == Double.POSITIVE_INFINITY) minFootDist = 0;

        return minHandDist + FOOT_HEURISTIC_WEIGHT * minFootDist;
    }

    /** 두 위치 간 이동 비용 계산 */
    private double calculateMoveCost(Map<String, Integer> from, Map<String, Integer> to, List<Hold> holds, HoldGeometryDTO geom,double perPixel) {
        double cost = 0;

        String[] limbs = {"leftHand", "rightHand", "leftFoot", "rightFoot"};
        for (String limb : limbs) {
            Integer fromHold = from.get(limb);
            Integer toHold = to.get(limb);

            if (fromHold != null && toHold != null && !fromHold.equals(toHold)) {
                Hold fromObj = findHoldByNumber(holds, fromHold);
                Hold toObj = findHoldByNumber(holds, toHold);
                cost += calculateDistance(fromObj, toObj, geom, perPixel);
            }
        }

        return cost;
    }

    /** A* 결과를 통해 실제 이동 경로 재구성 */
    private List<Map<String, Integer>> reconstructPath(AStarNode goalNode) {
        List<Map<String, Integer>> path = new ArrayList<>();
        AStarNode current = goalNode;

        while (current != null) {
            path.add(0, current.position);
            current = current.parent;
        }

        return path;
    }

    /** 위치 정보를 문자열 키로 변환 (중복 체크용) */
    private String positionKey(Map<String, Integer> position) {
        return String.format("%d,%d,%d,%d",
                position.get("leftHand"),
                position.get("rightHand"),
                position.get("leftFoot"),
                position.get("rightFoot"));
    }

    // ========== 검증 로직 ==========

    /** 목표 홀드에 도달했는지 확인 */
    private boolean hasReachedEnd(Map<String, Integer> position, List<Integer> endHolds) {
        Integer leftHand = position.get("leftHand");
        Integer rightHand = position.get("rightHand");

        // 양손 모두 도달 해야 종료
        return leftHand  != null
                && rightHand != null
                && endHolds.contains(leftHand)
                && endHolds.contains(rightHand);
    }

    /** 현재 자세가 사용자의 신체 조건에 맞는지 검증 */
    private boolean isPoseValid(Map<String, Integer> position,
                                User user,
                                List<Hold> holds,
                                HoldGeometryDTO geom,
                                double perPixel) {
        Integer leftHandId = position.get("leftHand");
        Integer rightHandId = position.get("rightHand");
        Integer leftFootId = position.get("leftFoot");
        Integer rightFootId = position.get("rightFoot");

        if (leftHandId != null && rightHandId != null) {
            AABB lhAabb = geom.getAabbMap().get(leftHandId);
            AABB rhAabb = geom.getAabbMap().get(rightHandId);

            // AABB가 null이 아닌지 확인
            if (lhAabb != null && rhAabb != null) {
                double lhX = (lhAabb.getMinX() + lhAabb.getMaxX()) / 2;
                double rhX = (rhAabb.getMinX() + rhAabb.getMaxX()) / 2;

                // 왼손이 오른손보다 오른쪽에 있으면 유효하지 않은 자세로 판단
                if (lhX > rhX) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (leftFootId != null && rightFootId != null) {
            AABB lfAabb = geom.getAabbMap().get(leftFootId);
            AABB rfAabb = geom.getAabbMap().get(rightFootId);

            // AABB가 null이 아닌지 확인 (안정성 강화)
            if (lfAabb != null && rfAabb != null) {
                double lfX = (lfAabb.getMinX() + lfAabb.getMaxX()) / 2;
                double rfX = (rfAabb.getMinX() + rfAabb.getMaxX()) / 2;

                // 왼발이 오른발보다 오른쪽에 있으면 유효하지 않은 자세로 판단
                if (lfX > rfX) {
                    return false;
                }
            } else {
                // 홀드 ID는 있지만 AABB가 없는 경우, 유효하지 않다고 처리
                return false;
            }
        }

        // 현재 자세가 사용자의 신체 조건에 맞는지 확인
        // 팔 범위 체크와 균형 지점 체크 + 발 리치 체크
        return isReachable(position, user, holds, geom,perPixel)
                && isHeightReachable(position,user,holds, perPixel)
                && hasBalancedSupport(position);
    }

    /** 두 손 사이의 거리가 armSpan 내에 있는지 확인 */
    private boolean isReachable(Map<String, Integer> position, User user, List<Hold> holds, HoldGeometryDTO geom, double perPixel) {
        Integer leftHand = position.get("leftHand");
        Integer rightHand = position.get("rightHand");

        if (leftHand == null || rightHand == null) {
            return true; // 한 손만 사용하는 경우
        }

        Hold leftHold = findHoldByNumber(holds, leftHand);
        Hold rightHold = findHoldByNumber(holds, rightHand);

        double distance = calculateDistance(leftHold, rightHold, geom, perPixel);
        return distance <= user.getArmSpan();
    }

    /** 안정적인 움직임인지 확인 */
    private boolean isStableTransition(Map<String, Integer> from, Map<String, Integer> to, User user, List<Hold> holds, HoldGeometryDTO geom,double perPixel) {
        int changedCount = countChangedPositions(from, to);
        if (changedCount > 2) {
            return false;
        }

        // 이동 후 자세가 유효한지 확인
        return isPoseValid(to, user, holds, geom,perPixel);
    }


    // ========== 계산 로직 ==========
    /** 홀드 번호로 해당 홀드 객체를 찾음 */
    private Hold findHoldByNumber(List<Hold> holds, int holdNumber) {
        return holds.stream()
                .filter(hold -> hold.getHoldId() == holdNumber)
                .findFirst()
                .orElse(null);
    }

    /** 시작 홀드와 탑 홀드의 X좌표를 비교하여 움직임 순서를 결정 */
    private List<String> determineLimbSequence(ProblemData problem, HoldGeometryDTO geom) {
        List<Hold> allHolds = problem.getSelected();

        // 시작 홀드의 평균 X 좌표 계산
        double startX = problem.getStartHold().stream()
                .map(holdId -> findHoldByNumber(allHolds, holdId))
                .filter(Objects::nonNull)
                .map(hold -> geom.getAabbMap().get(hold.getHoldId()))
                .mapToDouble(aabb -> (aabb.getMinX() + aabb.getMaxX()) / 2.0)
                .average()
                .orElse(0.0); // 시작 홀드가 없으면 0 (이론상 발생하지 않음)

        // 탑 홀드의 평균 X 좌표 계산
        double endX = problem.getEndHold().stream()
                .map(holdId -> findHoldByNumber(allHolds, holdId))
                .filter(Objects::nonNull)
                .map(hold -> geom.getAabbMap().get(hold.getHoldId()))
                .mapToDouble(aabb -> (aabb.getMinX() + aabb.getMaxX()) / 2.0)
                .average()
                .orElse(0.0); // 탑 홀드가 없으면 0 (이론상 발생하지 않음)

        if (endX > startX) {
            log.info("탑 홀드가 오른쪽에 위치. 순서: 오른발 -> 왼발 -> 오른손 -> 왼손");
            return List.of("rightFoot", "leftFoot", "rightHand", "leftHand");
        } else {
            log.info("탑 홀드가 왼쪽에 위치. 순서: 왼발 -> 오른발 -> 왼손 -> 오른손");
            return List.of("leftFoot", "rightFoot", "leftHand", "rightHand");
        }
    }


    /** 두 홀드 사이 거리 계산 (AABB 필터 → 헐-헐 캐시)*/
    private double calculateDistance(Hold holdA, Hold holdB, HoldGeometryDTO geom, double perPixel) {
        int holdAId = holdA.getHoldId(), holdBId = holdB.getHoldId();
        AABB box1 = geom.getAabbMap().get(holdAId), box2 = geom.getAabbMap().get(holdBId);

        // AABB가 겹치지 않으면 박스 간 최소 거리
        if (!box1.intersects(box2)) {
            return box1.distanceTo(box2) * perPixel;
        }
        // 겹친다면 미리 캐싱된 헐 간 최소 거리 사용
        return geom.getCachedDist()
                .getOrDefault(new HoldPair(holdAId, holdBId), 0.0)
                * perPixel;
    }

    /** 홀드의 중심 좌표 계산 */
    private Coordinate findCenter(List<Coordinate> coordinates) {
        int sumX = 0, sumY = 0;
        for (Coordinate coord : coordinates) {
            sumX += coord.getX();
            sumY += coord.getY();
        }
        return Coordinate.builder()
                .x(sumX / coordinates.size())
                .y(sumY / coordinates.size())
                .build();
    }

    /** 이동 전후 변경된 위치 개수 계산 */
    private int countChangedPositions(Map<String, Integer> from, Map<String, Integer> to) {
        int count = 0;
        for (String key : from.keySet()) {
            if (!Objects.equals(from.get(key), to.get(key))) {
                count++;
            }
        }
        return count;
    }

    /** 발의 위치 구하기 */
    private double getBaseFootHeight(Map<String,Integer> pos, List<Hold> holds, double perPixel) {
        // 각 발 홀드 중심 Y 픽셀 좌표 → 실제 높이(cm) 변환
        double leftFootY_px  = Double.NEGATIVE_INFINITY;
        double rightFootY_px = Double.NEGATIVE_INFINITY;

        if(pos.get("leftFoot") != null){
            leftFootY_px  = findCenter(findHoldByNumber(holds, pos.get("leftFoot")).getCoordinates()).getY();
        }
        if(pos.get("rightFoot") != null){
            rightFootY_px = findCenter(findHoldByNumber(holds, pos.get("rightFoot")).getCoordinates()).getY();
        }
        // 둘 다 null 이면 바닥(0cm)으로 간주
        if (leftFootY_px == Double.NEGATIVE_INFINITY
                && rightFootY_px == Double.NEGATIVE_INFINITY) {
            return 0;
        }

        double effectiveLeftY  = (leftFootY_px  == Double.NEGATIVE_INFINITY
                ? rightFootY_px : leftFootY_px);
        double effectiveRightY = (rightFootY_px == Double.NEGATIVE_INFINITY
                ? leftFootY_px  : rightFootY_px);

        double leftH  = effectiveLeftY * perPixel;
        double rightH = effectiveRightY * perPixel;
        return Math.max(leftH, rightH);
    }

    /** 신장 도달 여부 검사 */
    private boolean isHeightReachable(Map<String,Integer> pos, User user, List<Hold> holds, double perPixel) {

        //발 높이
        double baseFootHeight = getBaseFootHeight(pos, holds, perPixel);
        //어깨 높이
        double shoulderHeight = user.getHeight() * 0.80;   // cm
        double armLength      = user.getArmSpan() / 2.5;  // cm (양팔 전체 + 어깨 길이 → 한 팔 길이)

        // 가장 높은 손 홀드 높이
        Hold left = findHoldByNumber(holds, pos.get("leftHand"));
        Hold right= findHoldByNumber(holds, pos.get("rightHand"));
        double leftY  = findCenter(findHoldByNumber(holds, pos.get("leftHand")).getCoordinates()).getY();
        double rightY = findCenter(findHoldByNumber(holds, pos.get("rightHand")).getCoordinates()).getY();
        double maxHandH = Math.max(leftY, rightY) * perPixel;

        // 팔 뻗었을 때 닿을 수 있는 최대 높이
        double reachHeight    = baseFootHeight+ shoulderHeight + armLength;

        return maxHandH <= reachHeight;
    }

    /** 최소 3개의 팔다리가 홀드를 잡고 있는지 확인: 삼지점*/

    private boolean hasBalancedSupport(Map<String, Integer> position) {
        int supportPoints = 0;

        if (position.get("leftHand") != null) supportPoints++;
        if (position.get("rightHand") != null) supportPoints++;
        if (position.get("leftFoot") != null) supportPoints++;
        if (position.get("rightFoot") != null) supportPoints++;

        return supportPoints >= 3;
    }

    private double calculateBodyAngle(Map<String, Integer> position, HoldGeometryDTO geom) {
        // AABB 꺼내기
        AABB lh = geom.getAabbMap().get(position.get("leftHand"));
        AABB rh = geom.getAabbMap().get(position.get("rightHand"));
        AABB lf = geom.getAabbMap().get(position.get("leftFoot"));
        AABB rf = geom.getAabbMap().get(position.get("rightFoot"));

        // 중심점
        double handCenterX = (lh.getMinX() + lh.getMaxX()) / 2 + (rh.getMinX() + rh.getMaxX()) / 2;
        double handCenterY = (lh.getMinY() + lh.getMaxY()) / 2 + (rh.getMinY() + rh.getMaxY()) / 2;
        handCenterX /= 2; handCenterY /= 2;

        double footCenterX = (lf.getMinX() + lf.getMaxX()) / 2 + (rf.getMinX() + rf.getMaxX()) / 2;
        double footCenterY = (lf.getMinY() + lf.getMaxY()) / 2 + (rf.getMinY() + rf.getMaxY()) / 2;
        footCenterX /= 2; footCenterY /= 2;

        // 벡터 성분
        double dx = footCenterX - handCenterX;
        double dy = footCenterY - handCenterY;

        // 각도 계산
        double rad = Math.atan2(dy, dx);
        double deg = Math.toDegrees(rad);
        return (deg < 0) ? deg + 360 : deg;
    }

    /** A* 전용: 휴리스틱 무시, 물리·균형만 검사*/
    private List<Map<String,Integer>> generateAllValidMoves(
            Map<String,Integer> prevPos,
            Map<String,Integer> currPos,
            List<Hold> holds,
            User user,
            HoldGeometryDTO geom,
            Set<String> movedLimbs,
            double perPixel,
            List<String> limbSequence
    ) {
        Set<String> currentMoved = new LinkedHashSet<>(movedLimbs);
        if (movedLimbs.size() == limbSequence.size()) {
            movedLimbs.clear();
        }

        String nextLimb = limbSequence.stream()
                .filter(l -> !currentMoved.contains(l))
                .findFirst().orElse(null);
        if (nextLimb == null) return Collections.emptyList();

        List<Map<String,Integer>> candidates = new ArrayList<>();
        Integer currHoldId = currPos.get(nextLimb);
        Hold currHold = (currHoldId == null) ? null : findHoldByNumber(holds, currHoldId);
        double currY = (currHold == null) ? Double.POSITIVE_INFINITY : findCenter(currHold.getCoordinates()).getY();

        for (Hold h : holds) {
            if (Objects.equals(h.getHoldId(), currHoldId)) continue;

            //발의 경우 아래로 내려가는 후보 배제
            if (nextLimb.endsWith("Foot")) {
                double newY = findCenter(h.getCoordinates()).getY();
                if (currHoldId != null && newY > currY) {
                    continue;
                }
            }
            Map<String,Integer> cand = candidatePositionWith(currPos, nextLimb, h.getHoldId());
            if (isStableTransition(currPos, cand, user, holds, geom,perPixel)) {
                candidates.add(cand);
            }
        }
        return candidates;
    }


    /** 헬퍼 클래스: 2D 점 */
    public static class Point {
        final double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    /** 헬퍼 클래스: 복사본에 특정 팔다리만 교체 */
    private Map<String,Integer> candidatePositionWith(
            Map<String,Integer> currentPosition,
            String limb,
            Integer newHoldId
    ) {
        Map<String,Integer> positionCopy = new HashMap<>(currentPosition);
        positionCopy.put(limb, newHoldId);
        return positionCopy;
    }
    private static class ConvexHull {

        /** 입력 점 리스트에서 컨벡스 헐(볼록껍질) 점 리스트를 반환 */
        public static List<Point> compute(List<Point> pts) {
            if (pts.size() <= 1) return new ArrayList<>(pts);
            // 1) x, y 오름차순 정렬
            pts.sort(Comparator.comparingDouble((Point p) -> p.x)
                    .thenComparingDouble(p -> p.y));
            List<Point> lower = new ArrayList<>();
            for (Point p : pts) {
                while (lower.size() >= 2 &&
                        cross(lower.get(lower.size()-2), lower.get(lower.size()-1), p) <= 0) {
                    lower.remove(lower.size()-1);
                }
                lower.add(p);
            }
            List<Point> upper = new ArrayList<>();
            for (int i = pts.size()-1; i >= 0; i--) {
                Point p = pts.get(i);
                while (upper.size() >= 2 &&
                        cross(upper.get(upper.size()-2), upper.get(upper.size()-1), p) <= 0) {
                    upper.remove(upper.size()-1);
                }
                upper.add(p);
            }
            // 마지막 점 중복 제거
            lower.remove(lower.size()-1);
            upper.remove(upper.size()-1);
            lower.addAll(upper);
            return lower;
        }

        /** 세 점 a→b→c 의 방향 판정 (cross product) */
        private static double cross(Point a, Point b, Point c) {
            return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);
        }

        /** 두 헐 간 최소 거리: 모든 edge ↔ vertex 거리 중 최소값을 구하는 단순 구현 */
        public static double minDistanceBetween(List<Point> hull1, List<Point> hull2) {
            double min = Double.POSITIVE_INFINITY;
            // 1) hull1의 모든 점 ↔ hull2의 모든 선분
            for (Point p : hull1) {
                for (int i = 0; i < hull2.size(); i++) {
                    Point a = hull2.get(i);
                    Point b = hull2.get((i+1) % hull2.size());
                    min = Math.min(min, pointToSegmentDistance(p, a, b));
                }
            }
            // 2) hull2의 모든 점 ↔ hull1의 모든 선분
            for (Point p : hull2) {
                for (int i = 0; i < hull1.size(); i++) {
                    Point a = hull1.get(i);
                    Point b = hull1.get((i+1) % hull1.size());
                    min = Math.min(min, pointToSegmentDistance(p, a, b));
                }
            }
            return min;
        }

        /** 점 p와 선분 a→b 사이 최단 거리 */
        private static double pointToSegmentDistance(Point p, Point a, Point b) {
            double dx = b.x - a.x, dy = b.y - a.y;
            if (dx == 0 && dy == 0) {
                // a, b가 같은 점
                return Math.hypot(p.x - a.x, p.y - a.y);
            }
            // 투영 계수 t
            double t = ((p.x - a.x)*dx + (p.y - a.y)*dy) / (dx*dx + dy*dy);
            t = Math.max(0, Math.min(1, t));
            // 투영점
            double projX = a.x + t*dx;
            double projY = a.y + t*dy;
            return Math.hypot(p.x - projX, p.y - projY);
        }
    }
    /** 헬퍼 : 마지막에 움직인 사지(림b)*/
    private String detectLastMovedLimb(
            Map<String,Integer> prevPos,
            Map<String,Integer> currPos
    ) {
        for (String limb : List.of("leftHand","rightHand","leftFoot","rightFoot")) {
            if (!Objects.equals(prevPos.get(limb), currPos.get(limb))) {
                return limb;
            }
        }
        return null;
    }

    /** 헬퍼 클래스: AABB (축 정렬 경계 박스)*/
    public static class AABB {
        final double minX, minY, maxX, maxY;
        private AABB(double minX,double minY,double maxX,double maxY) {
            this.minX=minX; this.minY=minY; this.maxX=maxX; this.maxY=maxY;
        }
        static AABB fromPoints(List<Point> pts) {
            double minX=Double.MAX_VALUE, minY=Double.MAX_VALUE,
                    maxX=Double.MIN_VALUE, maxY=Double.MIN_VALUE;
            for (Point p: pts) {
                minX = Math.min(minX, p.x);
                minY = Math.min(minY, p.y);
                maxX = Math.max(maxX, p.x);
                maxY = Math.max(maxY, p.y);
            }
            return new AABB(minX,minY,maxX,maxY);
        }
        boolean intersects(AABB o) {
            return !(o.minX > this.maxX || o.maxX < this.minX
                    || o.minY > this.maxY || o.maxY < this.minY);
        }
        double distanceTo(AABB o) {
            double dx = Math.max(0, Math.max(o.minX - this.maxX, this.minX - o.maxX));
            double dy = Math.max(0, Math.max(o.minY - this.maxY, this.minY - o.maxY));
            return Math.hypot(dx, dy);
        }

        public double getMaxX() {
            return maxX;
        }
        public double getMaxY() {
            return maxY;
        }

        public double getMinX() {
            return minX;
        }
        public double getMinY() {
            return minY;
        }
    }

    /** 헬퍼 클래스: 홀드 ID 쌍 키*/
    public static class HoldPair {
        final int a, b;
        HoldPair(int a,int b){this.a=a;this.b=b;}
        public boolean equals(Object o){
            if(!(o instanceof HoldPair))return false;
            HoldPair p=(HoldPair)o;return p.a==a && p.b==b;
        }
        public int hashCode(){return Objects.hash(a,b);}
    }

    // ========== 응답 생성 메소드 ==========
    /** 경로 정보를 응답 DTO 형식으로 변환 */
    private ProblemSolutionResponseDTO buildResponse(List<Map<String, Integer>> solutionPath, Map<String, Integer> initialPosition) {
        List<Integer> leftHandPath = new ArrayList<>();
        List<Integer> rightHandPath = new ArrayList<>();
        List<Integer> leftFootPath = new ArrayList<>();
        List<Integer> rightFootPath = new ArrayList<>();

        // 각 프레임마다 모든 부위의 상태를 저장
        for (Map<String, Integer> frame : solutionPath) {
            leftHandPath.add(frame.get("leftHand"));
            rightHandPath.add(frame.get("rightHand"));
            leftFootPath.add(frame.get("leftFoot"));
            rightFootPath.add(frame.get("rightFoot"));
        }

        return ProblemSolutionResponseDTO.builder()
                .leftHand(leftHandPath)
                .rightHand(rightHandPath)
                .leftFoot(leftFootPath)
                .rightFoot(rightFootPath)
                .build();
    }
}