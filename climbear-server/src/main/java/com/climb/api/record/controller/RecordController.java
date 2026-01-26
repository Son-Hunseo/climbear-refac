package com.climb.api.record.controller;

import com.climb.api.record.controller.swagger.RecordControllerSwagger;
import com.climb.api.record.domain.dto.request.SaveSuccessRouteRequestDTO;
import com.climb.api.record.domain.dto.response.GetDetailRecordResponseDTO;
import com.climb.api.record.domain.dto.response.GetMyRecordListResponseDTO;
import com.climb.api.record.domain.dto.response.GetSimilarRecordListResponseDTO;
import com.climb.api.record.service.RecordService;
import com.climb.common.response.CustomApiResponse;
import com.climb.common.response.MessageResponse;
import com.climb.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Record", description = "풀이 기록 관련 API")
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @RecordControllerSwagger.SaveMemberRouteApi
    @PostMapping()
    public ResponseEntity<CustomApiResponse<MessageResponse>> saveMemberRoute(@RequestBody SaveSuccessRouteRequestDTO requestDTO){
        Integer userId = SecurityUtil.getCurrentUserId();
        recordService.saveMemberRoute(userId, requestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("성공한 경로를 저장했습니다")));
    }

    @RecordControllerSwagger.SaveNonMemberRouteApi
    @PostMapping("/non-member")
    public ResponseEntity<CustomApiResponse<MessageResponse>> saveNonMemberRoute(@RequestBody SaveSuccessRouteRequestDTO requestDTO){
        recordService.saveNonMemberRoute(requestDTO);
        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("성공한 경로를 저장했습니다")));
    }

    @RecordControllerSwagger.FailProblemApi
    @PatchMapping("/fail/{problemId}")
    public ResponseEntity<CustomApiResponse<MessageResponse>> failRecord(@PathVariable("problemId") @Valid int problemId){
        Integer userId = SecurityUtil.getCurrentUserId();
        recordService.failRecord(userId, problemId);
        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("시도 횟수를 증가시켰습니다")));
    }

    @RecordControllerSwagger.GetDetailApi
    @GetMapping("/detail/{problemId}")
    public ResponseEntity<CustomApiResponse<List<GetDetailRecordResponseDTO>>> getDetail(@PathVariable("problemId") @Valid int problemId){
        List<GetDetailRecordResponseDTO> responseDTO = recordService.getDetail(problemId);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }

    @RecordControllerSwagger.GetMyRecordListApi
    @GetMapping("/list")
    public ResponseEntity<CustomApiResponse<List<GetMyRecordListResponseDTO>>> getMyRecordList(){
        Integer userId = SecurityUtil.getCurrentUserId();
        List<GetMyRecordListResponseDTO> responseDTO = recordService.getMyRecordList(userId);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }

    @RecordControllerSwagger.GetSimilarRecordListApi
    @GetMapping("/similar/{categoryId}")
    public ResponseEntity<CustomApiResponse<List<GetSimilarRecordListResponseDTO>>> getSimilarRecordList(@PathVariable("categoryId") @Valid int categoryId){
        Integer userId = SecurityUtil.getCurrentUserId();
        int testCategoryId = 4;
        List<GetSimilarRecordListResponseDTO> responseDTO = recordService.getSimilarRecordList(userId, testCategoryId);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }
}
