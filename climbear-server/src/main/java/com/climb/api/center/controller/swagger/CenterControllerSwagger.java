package com.climb.api.center.controller.swagger;

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
 * CenterController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 CenterController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */

@Tag(name = "Center", description = "클라이밍 센터 API - 클라이밍 센터와 관련된 기능을 수행합니다")
public interface CenterControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "센터 리스트 조회",
            description = "등록된 모든 클라이밍 센터 목록을 조회합니다. 센터가 없는 경우 빈 배열을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "센터 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "센터 목록 조회 성공",
                                                    summary = "센터 목록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"centerId\": 1,\n      \"name\": \"클라임 클라이밍 센터\",\n      \"address\": \"서울시 강남구 논현동 123-45\",\n      \"latitude\": 37.5113, \n      \"longitude\": 127.0215\n    },\n    {\n      \"centerId\": 2,\n      \"name\": \"하이 클라이밍\",\n      \"address\": \"인천시 부평구 부평동 678-90\",\n      \"latitude\": 37.4905, \n      \"longitude\": 126.7244\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "센터 목록 없음",
                                                    summary = "등록된 클라이밍 센터가 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [],\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetCenterListApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "센터별 나의 기록 조회",
            description = "사용자별 클라이밍 센터 기록을 조회합니다. 센터명, 날짜, 레벨(컬러)별로 해결한 문제의 개수를 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "센터별 기록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "센터별 기록 조회 성공",
                                                    summary = "센터별 기록 조회 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [\n    {\n      \"centerName\": \"클라임 클라이밍 센터\",\n      \"dailyProblem\": [\n        {\n          \"date\": \"2025-05-01\",\n          \"levelCount\": [\n            {\n              \"level\": \"RED\",\n              \"count\": 3\n            },\n            {\n              \"level\": \"BLACK\",\n              \"count\": 1\n            }\n          ]\n        }\n      ]\n    },\n    {\n      \"centerName\": \"하이 클라이밍\",\n      \"dailyProblem\": [\n        {\n          \"date\": \"2025-05-03\",\n          \"levelCount\": [\n            {\n              \"level\": \"BLUE\",\n              \"count\": 2\n            }\n          ]\n        }\n      ]\n    }\n  ],\n  \"error\": null\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "기록 없음",
                                                    summary = "사용자의 센터별 기록이 없는 경우",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": [],\n  \"error\": null\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface GetMyCenterRecordApi {}
}
