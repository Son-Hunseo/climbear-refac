package com.example.climbear.util

import android.content.Context
import org.json.JSONObject


data class Box(
    val id: Int,
    val points: List<Point>
)

data class Point(
    val x: Int,
    val y: Int
)

/**
 * BoxUtils: assets/color_boxes.json 파일을 읽어 List<Box> 형태로 반환
 * 각 항목은 [id, [x,y], [x,y], ...] 구조로, 좌표 개수에 제약이 없음.
 */
object BoxUtils {
    /**
     * @param context Context 앱의 Context
     * @param assetFileName assets 폴더 내 JSON 파일명 (기본: "color_boxes.json")
     * @return JSON 내 "target" 배열을 파싱한 Box 리스트
     */
    fun loadBoxes(
        context: Context,
        assetFileName: String = "color_boxes.json"
    ): List<Box> {
        // 1) JSON 문자열 로드
        val jsonStr = context.assets.open(assetFileName)
            .bufferedReader()
            .use { it.readText() }

        // 2) JSONObject 파싱 및 "target" 배열 접근
        val jsonArray = JSONObject(jsonStr).getJSONArray("target")

        // 3) 각 항목을 순회하며 Box 생성
        return List(jsonArray.length()) { index ->
            val item = jsonArray.getJSONArray(index)

            // 첫 요소: ID
            val id = item.getInt(0)

            // 1번째부터 끝까지: [x, y] 쌍을 Point 로 변환
            val points = (1 until item.length()).map { idx ->
                val coord = item.getJSONArray(idx)
                Point(coord.getInt(0), coord.getInt(1))
            }

            Box(id = id, points = points)
        }
    }
}