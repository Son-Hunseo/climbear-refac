package com.climb.api.user.controller.swagger;

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
 * UserController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 UserController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */
@Tag(name = "User", description = "유저 API - 유저 관련 기능을 수행합니다")
public interface UserControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "회원 정보 조회",
            description = "로그인 한 사용자의 이메일, 닉네임, 키, 팔 길이, 경험치 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "사용자 정보 조회 성공",
                                                    summary = "사용자 정보 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"email\": \"test@test.com\",\n  \"nickname\": \"최강삼성\",\n  \"height\": 160.0,\n  \"armSpan\": 150.0,\n  \"exp\": 43875\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 사용자",
                                                    summary = "존재하지 않는 사용자",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetUserInfoApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "신체 정보 저장/수정",
            description = "사용자의 키(height)와 팔 길이(armSpan) 정보를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 정보 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "회원 정보 수정 성공",
                                                    summary = "키와 팔 길이 모두 성공적으로 수정됨",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": \"회원 정보 수정을 성공했습니다\",\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효하지 않은 입력값",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "키 유효성 검사 실패",
                                                    summary = "키가 120cm 미만인 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"키는 120cm 이상이어야 합니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "팔 길이 유효성 검사 실패",
                                                    summary = "팔 길이가 100cm 미만인 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"팔 길이는 100cm 이상이어야 합니다\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "잘못된 요청 - 존재하지 않는 회원",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 회원",
                                                    summary = "요청한 회원 ID가 존재하지 않는 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SetUserInfoApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "회원 탈퇴",
            description = "사용자 계정을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "회원 탈퇴 성공",
                                                    summary = "회원 탈퇴 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"회원 탈퇴 성공\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 사용자",
                                                    summary = "존재하지 않는 사용자",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface DeleteUserApi {
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "경험치 조회",
            description = "로그인한 사용자의 현재 경험치, 현재 레벨의 최대 경험치, 현재 레벨 이름, 다음 레벨 이름을 조회합니다. 사용자가 최고 레벨인 경우, 다음 레벨 이름(nextLevelName)은 현재 레벨 이름과 동일합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자 경험치 및 레벨 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "사용자 경험치 및 레벨 정보 조회 성공",
                                                    summary = "사용자 경험치 및 레벨 정보 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n  \"exp\": 5000,\n  \"maxExp\": 8000,\n  \"levelName\": \"orange\",\n  \"nextLevelName\": \"green\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 사용자",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "존재하지 않는 사용자",
                                                    summary = "존재하지 않는 사용자",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetUserExpApi {
    }
}
