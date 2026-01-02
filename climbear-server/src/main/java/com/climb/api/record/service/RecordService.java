package com.climb.api.record.service;

import com.climb.api.problem.domain.entity.Category;
import com.climb.api.problem.domain.entity.Problem;
import com.climb.api.problem.domain.entity.ProblemData;
import com.climb.api.problem.repository.CategoryRepository;
import com.climb.api.problem.repository.ProblemDataRepository;
import com.climb.api.problem.repository.ProblemRepository;
import com.climb.api.record.domain.dto.request.SaveSuccessRouteRequestDTO;
import com.climb.api.record.domain.dto.response.GetDetailRecordResponseDTO;
import com.climb.api.record.domain.dto.response.GetMyRecordListResponseDTO;
import com.climb.api.record.domain.dto.response.GetSimilarRecordListResponseDTO;
import com.climb.api.record.domain.entity.SolvedRoute;
import com.climb.api.record.repository.SolvedRouteRepository;
import com.climb.api.user.domain.entity.User;
import com.climb.api.user.domain.type.ClimbingLevel;
import com.climb.api.user.repository.UserRepository;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SolvedRouteRepository solvedRouteRepository;
    private final ProblemDataRepository problemDataRepository;
    private final CategoryRepository categoryRepository;

    // 회원의 성공 풀이 저장
    @Transactional
    public void saveMemberRoute(int userId, SaveSuccessRouteRequestDTO requestDTO) {
        Problem problem = problemRepository.findByProblemIdAndUserId(requestDTO.problemId(), userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROBLEM_NOT_FOUND));
        problem.incrementTryCount();
        problem.incrementSuccessCount();
        problemRepository.save(problem);

        increaseExp(userId, problem.getCategory().getLevel());

        SolvedRoute solvedRoute = SolvedRoute.builder()
                .problemId(requestDTO.problemId())
                .userId(userId)
                .successRound(problem.getSuccessCount())
                .route(requestDTO.route())
                .time(requestDTO.time())
                .height(requestDTO.height())
                .level(problem.getCategory().getLevel())
                .centerName(problem.getCategory().getCenter().getName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        solvedRouteRepository.save(solvedRoute);
    }

    // 회원이 성공한 문제의 레벨에 해당하는 경험치 증가
    private void increaseExp(int userId, String level){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ClimbingLevel climbingLevel = null;
        for (ClimbingLevel cl : ClimbingLevel.values()) {
            if (cl.getLevelName().equalsIgnoreCase(level)) {
                climbingLevel = cl;
                break;
            }
        }

        if (climbingLevel != null) {
            user.incrementExp(climbingLevel.getGainExp());
        }
        else {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 레벨은 존재하지 않습니다");
        }

        userRepository.save(user);
    }

    // 비회원의 성공 풀이 저장 (풀이 데이터 수집)
    @Transactional
    public void saveNonMemberRoute(SaveSuccessRouteRequestDTO requestDTO) {
        ProblemData problemData = problemDataRepository.findById(requestDTO.problemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROBLEM_NOT_FOUND));

        Category category = categoryRepository.findById(problemData.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "문제가 해당하는 카테고리를 찾을 수 없습니다"));

        SolvedRoute solvedRoute = SolvedRoute.builder()
                .problemId(requestDTO.problemId())
                .userId(null) // 비회원 기록 수집용
                .successRound(1) // 비회원이라 이전 기록이 없으므로 무조건 1회차
                .route(requestDTO.route())
                .time(requestDTO.time())
                .height(requestDTO.height()) // 비슷한 유저에게 추천하기 위해 저장
                .level(problemData.getLevel())
                .centerName(category.getCenter().getName())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        solvedRouteRepository.save(solvedRoute);
    }

    // 실패 했을 경우 시도 횟수만 증가
    @Transactional
    public void failRecord(Integer userId, Integer problemId) {
        Problem problem = problemRepository.findByProblemIdAndUserId(problemId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROBLEM_NOT_FOUND));
        problem.incrementTryCount();
        problemRepository.save(problem);
    }

    @Transactional(readOnly = true)
    public List<GetDetailRecordResponseDTO> getDetail(Integer problemId) {
        List<SolvedRoute> solvedRoutes = solvedRouteRepository.findByProblemId(problemId);
        if (solvedRoutes == null || solvedRoutes.isEmpty()) {
            return null;
        }

        ProblemData problemData = problemDataRepository.findById(problemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROBLEM_NOT_FOUND));

        return solvedRoutes.stream()
                .map(solvedRoute -> GetDetailRecordResponseDTO.builder()
                        .problemId(solvedRoute.getProblemId())
                        .successRound(solvedRoute.getSuccessRound())
                        .route(solvedRoute.getRoute())
                        .time(solvedRoute.getTime())
                        .height(solvedRoute.getHeight())
                        .createdAt(solvedRoute.getCreatedAt())
                        .imageName(problemData.getImageName())
                        .selected(problemData.getSelected())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GetMyRecordListResponseDTO> getMyRecordList(Integer userId) {
        List<SolvedRoute> solvedRoutes = solvedRouteRepository.findByUserId(userId);
        if(solvedRoutes == null || solvedRoutes.isEmpty()){
            return null;
        }

        // problemId로 SolvedRoute 그룹화
        Map<Integer, List<SolvedRoute>> routesByProblemId = solvedRoutes.stream()
                .collect(Collectors.groupingBy(SolvedRoute::getProblemId));

        // SolvedRoute가 있는 Problem 조회
        Set<Integer> attemptedProblemIds = routesByProblemId.keySet();
        List<Problem> problems = problemRepository.findByUserIdAndProblemIdIn(userId, attemptedProblemIds);

        List<GetMyRecordListResponseDTO> result = new ArrayList<>();

        for (Problem problem : problems) {
            Integer problemId = problem.getProblemId();

            // 문제별 SolvedRoute 목록
            List<SolvedRoute> problemRoutes = routesByProblemId.get(problemId);

            // 최소 소요 시간
            Integer minTime = problemRoutes.stream()
                    .filter(r -> r.getSuccessRound() > 0)
                    .map(SolvedRoute::getTime)
                    .min(Integer::compare)
                    .orElse(0);

            // 마지막 풀이 날짜 조회
            Timestamp lastSolvesDate = problemRoutes.stream()
                    .map(SolvedRoute::getCreatedAt)
                    .max(Timestamp::compareTo)
                    .orElse(null);

            GetMyRecordListResponseDTO responseDTO = GetMyRecordListResponseDTO.builder()
                    .problemId(problemId)
                    .level(problemRoutes.get(0).getLevel())
                    .successCount(problem.getSuccessCount())
                    .tryCount(problem.getTryCount())
                    .minTime(minTime)
                    .lastSolvesDate(lastSolvesDate)
                    .centerName(problemRoutes.get(0).getCenterName())
                    .build();

            result.add(responseDTO);
        }

        return result;
    }

    public List<GetSimilarRecordListResponseDTO> getSimilarRecordList(Integer userId, Integer categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Double userHeight = user.getHeight();
        if (userHeight == null) {
            userHeight = 165.0;
        }

        // 해당 categoryId에 소속된 problemId 조회
        List<Integer> problemIds = problemRepository.findProblemIdsByCategory_CategoryId(categoryId);

        // problemId가 problemIds에 있는 것 중 height가 userHeight와 +- 5cm인 SolvedRoute 가져오기
        List<SolvedRoute> solvedRoutes = solvedRouteRepository.findByProblemIdInAndHeightBetween(problemIds, userHeight - 5.0, userHeight + 5.0);

        // SolvedRoute의 userId, problemId 중복되는 경우 -> 같은 유저의 가장 최근 풀이만 선택
        Map<String, SolvedRoute> uniqueRoutes = new HashMap<>();

        solvedRoutes.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        // userId + problemId를 키로 사용하여 중복 제거
        for (SolvedRoute route : solvedRoutes) {
            String key = (route.getUserId() != null) ? route.getUserId() + "_" + route.getProblemId() : route.getId() + "_" + route.getProblemId();

            if (!uniqueRoutes.containsKey(key)) {
                uniqueRoutes.put(key, route);
            }
        }

        return uniqueRoutes.values().stream()
                .filter(item -> item.getUserId() == null || !item.getUserId().equals(userId)) // 본인 풀이 제외
                .map(item -> {
                    return GetSimilarRecordListResponseDTO.builder()
                            .problemId(item.getProblemId())
                            .userId(item.getUserId())
                            .height(item.getHeight())
                            .successRound(item.getSuccessRound())
                            .time(item.getTime())
                            .solvedDate(item.getCreatedAt())
                            .build();
                })
                .toList();
    }
}