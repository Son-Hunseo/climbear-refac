package com.climb.api.user.controller;

import com.climb.api.user.domain.dto.request.SetUserInfoRequestDTO;
import com.climb.api.user.domain.dto.response.UserExpResponseDTO;
import com.climb.api.user.domain.dto.response.UserInfoResponseDTO;
import com.climb.api.user.service.UserService;
import com.climb.common.response.CustomApiResponse;
import com.climb.api.user.controller.swagger.UserControllerSwagger;
import com.climb.common.response.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @UserControllerSwagger.GetUserInfoApi
    @GetMapping("/me")
    public ResponseEntity<CustomApiResponse<UserInfoResponseDTO>> getUserInfo(@RequestHeader("X-USER-ID") Integer userId){
        UserInfoResponseDTO response = userService.getUserInfo(userId);
        return ResponseEntity.ok(CustomApiResponse.success(response));
    }

    @UserControllerSwagger.SetUserInfoApi
    @PatchMapping("/info")
    public ResponseEntity<CustomApiResponse<String>> setUserInfo(@RequestHeader("X-USER-ID") Integer userId,
                                                             @RequestBody SetUserInfoRequestDTO request){
        CustomApiResponse<String> result = userService.setUserInfo(userId, request);

        if (result.getStatus() == CustomApiResponse.Status.SUCCESS) {
            return ResponseEntity.ok(result);
        }
        else {
            return ResponseEntity
                    .status(result.getError().getStatusCode())
                    .body(result);
        }
    }

    @UserControllerSwagger.DeleteUserApi
    @DeleteMapping
    public ResponseEntity<CustomApiResponse<MessageResponse>> deleteUser(@RequestHeader("X-USER-ID") Integer userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok(CustomApiResponse.success(MessageResponse.of("회원 탈퇴 성공")));
    }

    @UserControllerSwagger.GetUserExpApi
    @GetMapping("/exp")
    public ResponseEntity<CustomApiResponse<UserExpResponseDTO>> getExpUser(@RequestHeader("X-USER-ID") Integer userId){
        UserExpResponseDTO responseDTO = userService.getExpUser(userId);
        return ResponseEntity.ok(CustomApiResponse.success(responseDTO));
    }

}
