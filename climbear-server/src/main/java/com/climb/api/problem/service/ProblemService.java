package com.climb.api.problem.service;

import com.climb.api.center.domain.entity.Center;
import com.climb.api.problem.domain.dto.request.SaveProblemRequestDTO;
import com.climb.api.problem.domain.dto.response.SaveProblemResponseDTO;
import com.climb.api.problem.domain.entity.*;
import com.climb.api.problem.repository.CategoryRepository;
import com.climb.api.center.repository.CenterRepository;
import com.climb.api.problem.repository.ProblemDataRepository;
import com.climb.api.problem.repository.ProblemRepository;
import com.climb.api.user.domain.type.ClimbingLevel;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemDataRepository problemDataRepository;
    private final ProblemSequenceService problemSequenceService;
    private final ProblemRepository problemRepository;
    private final CenterRepository centerRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public SaveProblemResponseDTO saveMemberProblem(Integer userId, SaveProblemRequestDTO requestDTO){
        return saveProblem(userId, requestDTO);
    }

    @Transactional
    public SaveProblemResponseDTO saveNonMemberProblem(SaveProblemRequestDTO requestDTO){
        return saveProblem(null, requestDTO);
    }

    // 사용자가 풀 문제 저장
    public SaveProblemResponseDTO saveProblem(Integer userId, SaveProblemRequestDTO requestDTO) {

        // 입력 값 검증
        if(requestDTO.selected() == null || requestDTO.selected().isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "홀드 설정이 올바르지 않습니다");
        }
        else if(requestDTO.startHold() == null || requestDTO.startHold().isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작 홀드가 올바르지 않습니다");
        }
        else if(requestDTO.endHold() == null || requestDTO.endHold().isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "최종 홀드가 올바르지 않습니다");
        }
        else if(requestDTO.choiceColor() == null || requestDTO.choiceColor().isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "선택된 색상이 없습니다");
        }
        else if(requestDTO.level() == null || requestDTO.level().isEmpty()){
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "난이도가 선택되지 않았습니다");
        }

        // 문제 분류
        Category category = classifyCategory(requestDTO);

        // 문제 데이터 저장 - 동일한 사진이 아닌 경우 무조건 저장 (같은 문제라도 다른 좌표 정보를 가짐)
        int problemId = saveProblemData(category.getCategoryId(), requestDTO);

        // 유저인 경우 problem 생성
        if(userId != null){
            Problem problem = Problem.builder()
                    .problemId(problemId)
                    .category(category)
                    .userId(userId)
                    .build();

            problemRepository.save(problem);
        }

        return SaveProblemResponseDTO.builder()
                .problemId(problemId)
                .categoryId(category.getCategoryId())
                .build();
    }

    private Category classifyCategory(SaveProblemRequestDTO requestDTO){
        Center locatedCenter;

        // 사용자의 위치 정보가 없을 경우
        if(requestDTO.latitude() == null || requestDTO.longitude() == null){
            // 기존에 findById(0) 으로 되어있었다.
            // Center의 Id가 AutoIncrement로 1부터 시작하기 때문에 id 0인 센터는 있을 수 없기 때문에 수정했다.
            // 그래서 DB에는 디폴트 센터 즉, 더미를 id 1로 넣어두어야 한다.
            locatedCenter = centerRepository.findById(1)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "기본 클라이밍 센터가 설정되지 않았습니다"));
        }
        // 사용자의 위도/경도 50m 내외에 있는 암장 찾기
        else {
            locatedCenter = findLocatedCenter(requestDTO.latitude(), requestDTO.longitude());
            if (locatedCenter == null) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "등록된 클라이밍 센터가 없습니다");
            }
        }

        // Category에 있는 조건에 맞는 문제 찾기
        List<Category> candidateCategories = categoryRepository.findByCenterCenterIdAndHoldColor(locatedCenter.getCenterId(), requestDTO.choiceColor()).stream()
                .filter(category -> category.getLevel().equals(requestDTO.level())) // level 일치한 것
                .filter(category -> category.getHoldCount() == requestDTO.selected().size()) // 홀드 수 일치한 것
                .filter(category -> isWithinSizeRange(category.getHeightDiff(), requestDTO.heightDiff()) // 높이 차이가 20% 이내인 것
                        && isWithinSizeRange(category.getWidthDiff(), requestDTO.widthDiff())) // 폭 차이가 20% 이내인 것
                .toList();

        // 조건에 맞는 카테고리가 있으면 해당 category 반환
        if (!candidateCategories.isEmpty()) {
            return candidateCategories.get(0);
        }
        // 새로운 카테고리 생성 후 category 반환
        else {
            return saveNewCategory(locatedCenter, requestDTO);
        }
    }

    // 사용자가 위치한 센터 찾기 (반경 5km 이내) - ST_Distance_Sphere 사용
    private Center findLocatedCenter(Double latitude, Double longitude) {
        final double MAX_DISTANCE_METERS = 5000.0;
        List<Center> nearCenters = centerRepository.findCentersWithinDistance(latitude, longitude, MAX_DISTANCE_METERS);
        return nearCenters.isEmpty() ? null : nearCenters.get(0);
    }

    // 높이/폭 차이가 20% 이내인지 확인
    private boolean isWithinSizeRange(Double value1, Double value2) {
        if (value1 == null || value2 == null)
            return false;
        double diff = Math.abs(value1 - value2);
        double max = Math.max(value1, value2);
        return (diff / max) <= 0.2; // 20% 이내
    }

    // 새로운 카테고리 생성
    private Category saveNewCategory(Center center, SaveProblemRequestDTO requestDTO){
        // enum에 존재하는 level인지 확인
        String requestLevel = requestDTO.level().toUpperCase();
        boolean isValidLevel = false;

        for (ClimbingLevel climbingLevel : ClimbingLevel.values()) {
            if (climbingLevel.getLevelName().equalsIgnoreCase(requestLevel)) {
                isValidLevel = true;
                break;
            }
        }

        if (!isValidLevel) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 레벨은 존재하지 않습니다");
        }

        // 새로운 문제 분류 생성
        Category newCategory = Category.builder()
                .center(center)
                .holdColor(requestDTO.choiceColor().toUpperCase())
                .level(requestDTO.level().toUpperCase())
                .holdCount(requestDTO.selected().size())
                .heightDiff(requestDTO.heightDiff())
                .widthDiff(requestDTO.widthDiff())
                .build();

        return categoryRepository.save(newCategory);
    }

    // 문제 데이터 저장
    private int saveProblemData(Integer categoryId, SaveProblemRequestDTO requestDTO) {
        int nextProblemId = problemSequenceService.getNextSequence("problem_id");

        ProblemData problemData = ProblemData.builder()
                .problemId(nextProblemId)
                .categoryId(categoryId)
                .imageName(requestDTO.imageName())
                .selected(requestDTO.selected())
                .startHold(requestDTO.startHold())
                .endHold(requestDTO.endHold())
                .choiceColor(requestDTO.choiceColor().toUpperCase())
                .level(requestDTO.level().toUpperCase())
                .pixelGrid(requestDTO.pixelGrid())
                .build();

        problemDataRepository.save(problemData);

        return nextProblemId;
    }
}
