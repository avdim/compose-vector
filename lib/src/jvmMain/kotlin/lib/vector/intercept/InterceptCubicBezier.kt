package lib.vector.intercept

import lib.vector.*

data class InterceptedBezierPoint(val relativePointsA: List<Float>, val relativePointsB: List<Float>, val points: List<Pt>)

fun InterceptedBezierPoint.mapA(lambda: (Float) -> Float) = copy(
  relativePointsA = relativePointsA.map(lambda)
)

fun InterceptedBezierPoint.mapB(lambda: (Float) -> Float) = copy(
  relativePointsB = relativePointsB.map(lambda)
)

operator fun InterceptedBezierPoint.plus(other: InterceptedBezierPoint) =
  InterceptedBezierPoint(
    relativePointsA = relativePointsA + other.relativePointsA,
    relativePointsB = relativePointsB + other.relativePointsB,
    points = points + other.points
  )

fun interceptCubicBezier(a: BezierSegment, b: BezierSegment): InterceptedBezierPoint {
  val precision = minOf(a.lengthF, b.lengthF) / 1E5f
  return interceptCubicBezier(a, b, precision)
}

fun interceptCubicBezier(a: BezierSegment, b: BezierSegment, precision: Float): InterceptedBezierPoint = if (a.aabb intercepted b.aabb) {
  if (a.aabb.size.diagonalF < precision) {
    InterceptedBezierPoint(listOf(0.5f), listOf(0.5f), listOf(a.aabb.topLeft))
  } else {
    val (a1, a2) = a.split(0.5f)
    val (b1, b2) = b.split(0.5f)
    interceptCubicBezier(a1, b1, precision).mapA { it / 2 }.mapB { it / 2 } +
      interceptCubicBezier(a1, b2, precision).mapA { it / 2 }.mapB { 0.5f + it / 2 } +
      interceptCubicBezier(a2, b1, precision).mapA { 0.5f + it / 2 }.mapB { it / 2 } +
      interceptCubicBezier(a2, b2, precision).mapA { 0.5f + it / 2 }.mapB { 0.5f + it / 2 }
  }
} else {
  InterceptedBezierPoint(emptyList(), emptyList(), emptyList())
}

infix fun Rect.intercepted(b: Rect): Boolean {
  val a = this
  val notIntercepter = a.top >= b.bottom || b.top >= a.bottom || a.left >= b.right || b.left >= a.right
  return notIntercepter.not()
}
