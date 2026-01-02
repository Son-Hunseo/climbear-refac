package com.climb.api.solution.controller;

import com.climb.api.solution.controller.swagger.SolutionControllerSwagger;
import com.climb.api.solution.domain.dto.request.FailureRequestDTO;
import com.climb.api.solution.domain.dto.request.NonMemberFailureRequestDTO;
import com.climb.api.solution.domain.dto.request.NonMemberProblemRequestDTO;
import com.climb.api.solution.domain.dto.response.ProblemSolutionResponseDTO;
import com.climb.api.solution.service.SolutionService;
import com.climb.common.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Solution", description = "솔루션 관련 API")
@RestController
@RequestMapping("/api/v1/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;

    // 홀드 데이터로 솔루션 제공
    @SolutionControllerSwagger.GetProblemSolutionApi
    @GetMapping("/problem/{problemId}")
    public ResponseEntity<CustomApiResponse<ProblemSolutionResponseDTO>> getProblemSolution(@RequestHeader("X-USER-ID") Integer userId,
                                                                                            @PathVariable("problemId") @Valid int problemId){
        ProblemSolutionResponseDTO response = solutionService.getProblemSolution(userId, problemId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    // 회원이 설정한 실패 지점에서 솔루션 제공
    @SolutionControllerSwagger.GetFailureSolutionApi
    @PostMapping("/failure/{problemId}")
    public ResponseEntity<CustomApiResponse<ProblemSolutionResponseDTO>> getFailureSolution(@RequestHeader("X-USER-ID") Integer userId,
                                                                                            @PathVariable("problemId") @Valid int problemId,
                                                                                            @RequestBody FailureRequestDTO failureRequestDTO){
        ProblemSolutionResponseDTO response = solutionService.getFailureSolution(userId, problemId, failureRequestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @SolutionControllerSwagger.GetNonMemberProblemSolutionApi
    @PostMapping("/non-member/problem")
    public ResponseEntity<CustomApiResponse<ProblemSolutionResponseDTO>> getNonMemberProblemSolution(@RequestBody NonMemberProblemRequestDTO requestDTO){
        ProblemSolutionResponseDTO response = solutionService.getNonMemberProblemSolution(requestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @SolutionControllerSwagger.GetNonMemberFailureSolutionApi
    @PostMapping("/non-member/failure")
    public ResponseEntity<CustomApiResponse<ProblemSolutionResponseDTO>> getNonMemberFailureSolution(@RequestBody NonMemberFailureRequestDTO requestDTO){
        ProblemSolutionResponseDTO response = solutionService.getNonMemberFailureSolution(requestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

}
