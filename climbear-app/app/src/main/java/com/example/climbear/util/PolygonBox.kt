package com.example.climbear.util

import kotlin.math.roundToInt

/**
 * 단순 사각형 바운딩 박스
 */
data class Rect(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

/**
 * 폴리곤 박스: 원본 꼭짓점(points)을 유지하면서
 * inset 된 바운딩 박스(rect)를 함께 보관합니다.
 */
data class PolygonBox(
    val id: Int,
    val points: List<Point>,
    val bbox: Rect
) {
    companion object {
        /**
         * BoxUtils.loadBoxes() 로 생성된 Box(id, points) 와 inset 퍼센트를 받아
         * bounding box 를 계산해 PolygonBox 로 변환합니다.
         */
        fun from(box: Box, insetRatio: Float): PolygonBox {
            val xs = box.points.map { it.x }
            val ys = box.points.map { it.y }
            val xMin = xs.minOrNull() ?: 0
            val xMax = xs.maxOrNull() ?: 0
            val yMin = ys.minOrNull() ?: 0
            val yMax = ys.maxOrNull() ?: 0

            val padX = ((xMax - xMin) * insetRatio).roundToInt()
            val padY = ((yMax - yMin) * insetRatio).roundToInt()

            val rect = Rect(
                left   = xMin + padX,
                top    = yMin + padY,
                right  = xMax - padX,
                bottom = yMax - padY
            )
            return PolygonBox(box.id, box.points, rect)
        }
        /**
         * Ray-casting 알고리즘을 이용한 point-in-polygon 검사
         */
        fun pointInPolygon(poly: List<Point>, x: Int, y: Int): Boolean {
            var inside = false
            var j = poly.size - 1
            for (i in poly.indices) {
                val xi = poly[i].x; val yi = poly[i].y
                val xj = poly[j].x; val yj = poly[j].y

                val isBetweenY = ((yi > y) != (yj > y))

                val intersect = isBetweenY &&
                        (x < (xj - xi).toDouble() * (y - yi) / (yj - yi) + xi)

                if (intersect) {
                    inside = !inside
                }
                j = i
            }
            return inside
        }
    }
}