package com.climb.api.solution.controller.swagger;

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
 * SolutionController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 SolutionController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */

@Tag(name = "Solution", description = "솔루션 API - 클라이밍 문제 해결 솔루션 제공 기능을 수행합니다")
public interface SolutionControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "문제 솔루션 요청 (회원)",
            description = "특정 클라이밍 문제에 대한 솔루션을 제공합니다. 회원의 신체 정보와 문제 데이터를 기반으로 최적의 경로를 계산합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "솔루션 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "솔루션 조회 성공",
                                                    summary = "클라이밍 문제 솔루션 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"leftHand\": [1, 3, 5, 7],\n    \"rightHand\": [2, 4, 6, 8],\n    \"leftFoot\": [9, 11, 13, 15],\n    \"rightFoot\": [10, 12, 14, 16]\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetProblemSolutionApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "실패 지점 솔루션 요청 (비회원)",
            description = "회원이 설정한 실패 지점부터 클라이밍 문제를 해결하기 위한 솔루션을 제공합니다. 현재 실패한 위치에서 최적의 경로를 계산합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "솔루션 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "실패 지점 솔루션 조회 성공",
                                                    summary = "실패 지점부터 솔루션 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"leftHand\": [3, 5, 7],\n    \"rightHand\": [4, 6, 8],\n    \"leftFoot\": [11, 13, 15],\n    \"rightFoot\": [12, 14, 16]\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetFailureSolutionApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "문제 솔루션 요청 (회원)",
            description = "비회원을 위한 클라이밍 문제 솔루션을 제공합니다. 사용자가 입력한 신체 정보와 문제 데이터를 기반으로 최적의 경로를 계산합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "솔루션 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "비회원 솔루션 조회 성공",
                                                    summary = "비회원 클라이밍 문제 솔루션 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"leftHand\": [1, 3, 5, 7],\n    \"rightHand\": [2, 4, 6, 8],\n    \"leftFoot\": [9, 11, 13, 15],\n    \"rightFoot\": [10, 12, 14, 16]\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetNonMemberProblemSolutionApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "실패 지점 솔루션 요청 (비회원)",
            description = "비회원이 설정한 실패 지점부터 클라이밍 문제를 해결하기 위한 솔루션을 제공합니다. 입력한 신체 정보와 실패 위치를 기반으로 최적의 경로를 계산합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "솔루션 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "비회원 실패 지점 솔루션 조회 성공",
                                                    summary = "비회원 실패 지점 솔루션 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"leftHand\": [3, 5, 7],\n    \"rightHand\": [4, 6, 8],\n    \"leftFoot\": [11, 13, 15],\n    \"rightFoot\": [12, 14, 16]\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetNonMemberFailureSolutionApi {}
}