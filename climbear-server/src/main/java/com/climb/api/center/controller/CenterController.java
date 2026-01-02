package com.climb.api.center.controller;

import com.climb.api.center.controller.swagger.CenterControllerSwagger;
import com.climb.api.center.domain.dto.response.CenterRecordResponseDTO;
import com.climb.api.center.domain.entity.Center;
import com.climb.api.center.service.CenterService;
import com.climb.common.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Center", description = "클라이밍 센터 관련 API")
@RestController
@RequestMapping("/api/v1/centers")
@RequiredArgsConstructor
public class CenterController {

    private final CenterService centerService;

    @CenterControllerSwagger.GetCenterListApi
    @GetMapping("/list")
    public ResponseEntity<CustomApiResponse<List<Center>>> getCenterList(){
        List<Center> response = centerService.getCenterList();
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @CenterControllerSwagger.GetMyCenterRecordApi
    @GetMapping("/my")
    public ResponseEntity<CustomApiResponse<List<CenterRecordResponseDTO>>> getMyCenterRecord(@RequestHeader("X-USER-ID") Integer userId){
        List<CenterRecordResponseDTO> response = centerService.getMyCenterRecord(userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

}
