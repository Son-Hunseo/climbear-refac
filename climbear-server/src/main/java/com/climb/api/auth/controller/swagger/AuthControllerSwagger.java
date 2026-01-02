package com.climb.api.auth.controller.swagger;

import com.climb.common.exception.ErrorResponse;
import com.climb.common.response.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AuthController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 AuthController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */

@Tag(name = "Auth", description = "인증/인가 API - 인증/인가와 관련된 기능을 수행합니다")
public interface AuthControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "토큰 갱신",
            description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "토큰 갱신 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "토큰 갱신 성공",
                                                    summary = "유효한 리프레시 토큰을 통한 토큰 갱신 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "유효하지 않은 리프레시 토큰 또는 만료된 리프레시 토큰",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "만료된 리프레시 토큰",
                                                    summary = "리프레시 토큰이 만료된 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"리프레시 토큰이 만료되었습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 리프레시 토큰",
                                                    summary = "존재하지 않거나 유효하지 않은 리프레시 토큰",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 401,\n    \"message\": \"유효하지 않은 리프레시 토큰입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "사용자를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "사용자를 찾을 수 없음",
                                                    summary = "해당 ID의 사용자가 존재하지 않는 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface RefreshTokenApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "카카오 로그인",
            description = "카카오 액세스 토큰을 사용하여 로그인 또는 회원가입을 진행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "기존 사용자 로그인",
                                                    summary = "기존 사용자 로그인 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"userId\": 1,\n    \"email\": \"test@test.com\",\n    \"nickname\": \"김싸피\",\n    \"isNewUser\": false\n  },\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "신규 사용자 회원가입",
                                                    summary = "신규 사용자 회원가입 및 로그인 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\n    \"userId\": 1,\n    \"email\": \"test@test.com\",\n    \"nickname\": \"김싸피\",\n    \"isNewUser\": true\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 입력값",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "유효하지 않은 카카오 토큰",
                                                    summary = "카카오 액세스 토큰이 유효하지 않은 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"errorCode\": \"400\",\n    \"message\": \"유효하지 않은 입력값입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "카카오 API 호출 중 오류 발생",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "카카오 API 오류",
                                                    summary = "카카오 API 호출 중 오류가 발생한 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"errorCode\": \"500\",\n    \"message\": \"카카오 API 호출 중 오류가 발생했습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface KakaoLoginApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "로그아웃",
            description = "사용자의 리프레시 토큰을 삭제하여, 로그아웃 처리합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "로그아웃 성공 (콘텐츠 없음)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증되지 않은 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "인증되지 않은 사용자",
                                                    summary = "인증되지 않은 사용자가 로그아웃을 시도한 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"errorCode\": \"401\",\n    \"message\": \"인증되지 않은 접근입니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface LogoutApi {}
}
