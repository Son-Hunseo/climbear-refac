package com.climb.api.problem.controller;

import com.climb.api.problem.controller.swagger.ProblemControllerSwagger;
import com.climb.api.problem.domain.dto.request.SaveProblemRequestDTO;
import com.climb.api.problem.domain.dto.response.SaveProblemResponseDTO;
import com.climb.api.problem.service.ProblemService;
import com.climb.common.response.CustomApiResponse;
import com.climb.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Problem", description = "클라이밍 문제 관련 API")
@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @ProblemControllerSwagger.SaveMemberProblemApi
    @PostMapping()
    public ResponseEntity<CustomApiResponse<SaveProblemResponseDTO>> saveMemberProblem(@RequestBody SaveProblemRequestDTO saveProblemRequestDTO){
        Integer userId = SecurityUtil.getCurrentUserId();
        SaveProblemResponseDTO responseDTO = problemService.saveMemberProblem(userId, saveProblemRequestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }

    @ProblemControllerSwagger.SaveNonMemberProblemApi
    @PostMapping("/non-member")
    public ResponseEntity<CustomApiResponse<SaveProblemResponseDTO>> saveNonMemberProblem(@RequestBody SaveProblemRequestDTO saveProblemRequestDTO){
        SaveProblemResponseDTO responseDTO = problemService.saveNonMemberProblem(saveProblemRequestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }
}
