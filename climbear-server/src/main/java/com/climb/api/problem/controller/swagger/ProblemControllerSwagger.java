package com.climb.api.problem.controller.swagger;

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
 * ProblemController의 Swagger 문서화를 위한 인터페이스
 * 이 인터페이스는 ProblemController의 API 메서드에 대한 Swagger 문서화 정보만 포함합니다.
 */

@Tag(name = "Problem", description = "클라이밍 문제 API - 클라이밍 문제 관련 기능을 수행합니다")
public interface ProblemControllerSwagger {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "문제 등록 (회원)",
            description = "클라이밍 문제를 등록합니다. 홀드 설정, 시작 홀드, 최종 홀드, 선택 색상이 필요합니다. 위도/경도 정보가 없는 경우 기본 클라이밍 센터(centerId=0)를 사용합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "문제 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 등록 성공",
                                                    summary = "문제 등록 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"problemId\": 123,\n    \"categoryId\": 45\n  },\n  \"error\": null\n}"
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
                                                    name = "selected 오류 예시",
                                                    summary = "selected가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"홀드 설정이 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "startHold 오류 예시",
                                                    summary = "시작 홀드가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시작 홀드가 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "endHold 오류 예시",
                                                    summary = "최종 홀드가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"최종 홀드가 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "choiceColor 오류 예시",
                                                    summary = "선택 색상이 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"선택된 색상이 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "level 오류 예시",
                                                    summary = "난이도가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"난이도가 선택되지 않았습니다\"\n  }\n}"
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
                                                    name = "센터 없음 오류 예시",
                                                    summary = "등록된 클라이밍 센터가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"등록된 클라이밍 센터가 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "기본 센터 없음 오류 예시",
                                                    summary = "기본 클라이밍 센터 설정 안 됨 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"기본 클라이밍 센터가 설정되지 않았습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 레벨 오류",
                                                    summary = "존재하지 않는 레벨 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 레벨은 존재하지 않습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SaveMemberProblemApi {}

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(
            summary = "문제 등록 (비회원)",
            description = "비회원이 클라이밍 문제를 등록합니다. 홀드 설정, 시작 홀드, 최종 홀드, 선택 색상이 필요합니다. 위도/경도 정보가 없는 경우 기본 클라이밍 센터(centerId=0)를 사용합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "문제 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CustomApiResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "문제 등록 성공",
                                                    summary = "문제 등록 성공",
                                                    value = "{\n  \"status\": \"SUCCESS\",\n  \"data\": {\n    \"problemId\": 123,\n    \"categoryId\": 45\n  },\n  \"error\": null\n}"
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
                                                    name = "selected 오류 예시",
                                                    summary = "selected가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"홀드 설정이 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "startHold 오류 예시",
                                                    summary = "시작 홀드가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"시작 홀드가 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "endHold 오류 예시",
                                                    summary = "최종 홀드가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"최종 홀드가 올바르지 않습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "choiceColor 오류 예시",
                                                    summary = "선택 색상이 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"선택된 색상이 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "level 오류 예시",
                                                    summary = "난이도가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 400,\n    \"message\": \"난이도가 선택되지 않았습니다\"\n  }\n}"
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
                                                    name = "센터 없음 오류 예시",
                                                    summary = "등록된 클라이밍 센터가 없는 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"등록된 클라이밍 센터가 없습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "기본 센터 없음 오류 예시",
                                                    summary = "기본 클라이밍 센터 설정 안 됨 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"기본 클라이밍 센터가 설정되지 않았습니다\"\n  }\n}"
                                            ),
                                            @ExampleObject(
                                                    name = "유효하지 않은 레벨 오류",
                                                    summary = "존재하지 않는 레벨 오류",
                                                    value = "{\n  \"status\": \"ERROR\",\n  \"data\": null,\n  \"error\": {\n    \"statusCode\": 404,\n    \"message\": \"해당 레벨은 존재하지 않습니다\"\n  }\n}"
                                            )
                                    }
                            )
                    )
            }
    )
    @interface SaveNonMemberProblemApi {}
}
