package com.climb.api.record.controller.swagger;

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
 * RecordController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 RecordController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */

@Tag(name = "Record", description = "풀이 기록 관련 API")
public interface RecordControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "풀이 저장 (회원)",
            description = "회원이 성공적으로 클라이밍 문제를 풀었을 때 경로 정보를 저장하고 문제의 시도 횟수와 성공 횟수를 증가시킵니다. 또한 문제의 난이도에 따라 사용자에게 경험치를 부여합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공한 경로 저장 및 경험치 부여 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "경로 저장 성공",
                                                    summary = "성공한 경로 저장 및 경험치 부여 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"성공한 경로를 저장했습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검증 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "요청 데이터 오류",
                                                    summary = "요청 데이터 형식 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"잘못된 요청 형식입니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "리소스를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 없음 오류",
                                                    summary = "등록된 문제가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 문제를 찾을 수 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "사용자 없음 오류",
                                                    summary = "등록된 사용자가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 사용자를 찾을 수 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "레벨 없음 오류",
                                                    summary = "존재하지 않는 클라이밍 레벨 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 레벨은 존재하지 않습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SaveMemberRouteApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "풀이 저장 (비회원)",
            description = "비회원이 성공적으로 클라이밍 문제를 풀었을 때 경로 정보만 저장합니다. 문제 통계에는 반영되지 않으며 풀이 데이터 수집을 위해 사용됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "비회원 경로 저장 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "경로 저장 성공",
                                                    summary = "성공한 경로 저장 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"성공한 경로를 저장했습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검증 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "요청 데이터 오류",
                                                    summary = "요청 데이터 형식 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"잘못된 요청 형식입니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "리소스를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 없음 오류",
                                                    summary = "등록된 문제가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 문제를 찾을 수 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "카테고리 없음 오류",
                                                    summary = "문제가 해당하는 카테고리를 찾을 수 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"문제가 해당하는 카테고리를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SaveNonMemberRouteApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "풀이 실패 처리",
            description = "클라이밍 문제 실패 시 해당 문제에 대한 시도 횟수를 증가시킵니다. 사용자가 생성한 문제에 대해서만 적용됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "시도 횟수 증가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "시도 횟수 증가",
                                                    summary = "실패 시도 횟수 증가 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"message\": \"시도 횟수를 증가시켰습니다\"\n  },\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "문제를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 없음 오류",
                                                    summary = "해당 사용자의 문제를 찾을 수 없는 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 문제를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface FailProblemApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "풀이 디테일 조회",
            description = "특정 문제에 대한 풀이 상세 기록을 조회합니다. 풀이 기록이 없는 경우 data에 null을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "문제 풀이 상세 기록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 풀이 기록 조회 성공",
                                                    summary = "문제 풀이 상세 기록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"problemId\": 1,\n      \"successRound\": 1,\n      \"route\": {\n        \"leftHand\": [\n          0\n        ],\n        \"rightHand\": [\n          0\n        ],\n        \"leftFoot\": [\n          0\n        ],\n        \"rightFoot\": [\n          0\n        ]\n      },\n      \"time\": 0,\n      \"height\": 175.5,\n      \"createdAt\": \"2025-05-15 14:10:16\",\n      \"imageName\": \"problem_image_001.jpg\",\n      \"selected\": [\n        {\n          \"holdId\": 1,\n          \"coordinates\": [\n            {\n              \"x\": 100,\n              \"y\": 200\n            },\n            {\n              \"x\": 110,\n              \"y\": 210\n            }\n          ]\n        },\n        {\n          \"holdId\": 2,\n          \"coordinates\": [\n            {\n              \"x\": 150,\n              \"y\": 300\n            }\n          ]\n        }\n      ]\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "풀이 기록 없음",
                                                    summary = "풀이 기록이 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": null,\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검증 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "유효성 검증 오류",
                                                    summary = "경로 변수 유효성 검증 실패",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"잘못된 요청 형식입니다.\"\n  }\n}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "문제를 찾을 수 없음",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 없음 오류",
                                                    summary = "해당 사용자의 문제를 찾을 수 없는 경우",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 문제를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetDetailApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "나의 풀이 리스트 조회",
            description = "로그인한 사용자의 모든 문제 풀이 기록 목록을 조회합니다. 각 문제별 성공 횟수, 시도 횟수, 최소 소요 시간, 마지막 풀이 날짜 등의 정보를 제공합니다. 기록이 없는 경우 빈 배열을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "문제 풀이 기록 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 풀이 기록 목록 조회 성공",
                                                    summary = "사용자의 문제 풀이 기록 목록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"problemId\": 1,\n      \"level\": \"red\",\n      \"successCount\": 2,\n      \"tryCount\": 5,\n      \"minTime\": 45,\n      \"lastSolvesDate\": \"2025-05-10 16:25:30\",\n      \"centerName\": \"클라임 클라이밍 센터\"\n    },\n    {\n      \"problemId\": 2,\n      \"level\": \"green\",\n      \"successCount\": 1,\n      \"tryCount\": 3,\n      \"minTime\": 60,\n      \"lastSolvesDate\": \"2025-05-12 18:30:15\",\n      \"centerName\": \"더 월 클라이밍\"\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "풀이 기록 없음",
                                                    summary = "사용자의 문제 풀이 기록이 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": null,\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetMyRecordListApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "유사한 유저 풀이 리스트 조회",
            description = "같은 카테고리에 속한 문제들에 대해 로그인한 사용자와 신체 조건(키)이 비슷한 다른 사용자들의 풀이 기록을 조회합니다. 키가 ±5cm 범위 내에 있는 사용자들의 기록을 제공합니다. 같은 유저의 가장 최근 풀이만 표시하며, 로그인한 사용자 본인의 풀이는 제외됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "비슷한 신체 조건의 유저 풀이 기록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "비슷한 신체 조건 유저 풀이 기록 조회 성공",
                                                    summary = "비슷한 신체 조건 유저 풀이 기록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"problemId\": 1,\n      \"userId\": 2,\n      \"height\": 175.5,\n      \"successRound\": 3,\n      \"time\": 45,\n      \"solvedDate\": \"2025-05-14 17:30:22\"\n    },\n    {\n      \"problemId\": 1,\n      \"userId\": 5,\n      \"height\": 172.8,\n      \"successRound\": 1,\n      \"time\": 60,\n      \"solvedDate\": \"2025-05-15 09:15:47\"\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "유사한 유저 풀이 기록 없음",
                                                    summary = "비슷한 신체 조건의 유저 풀이 기록이 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [],\n  \"error\": null\n}"
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
                                                    name = "사용자 없음 오류",
                                                    summary = "요청한 사용자를 찾을 수 없음",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"사용자를 찾을 수 없습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetSimilarRecordListApi {}
}