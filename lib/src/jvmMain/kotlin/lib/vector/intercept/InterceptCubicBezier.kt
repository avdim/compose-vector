package lib.vector.intercept

import lib.vector.*

data class InterceptedBezierPoint(val relativePointsA: List<Float>, val relativePointsB: List<Float>)

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
  )

fun interceptCubicBezier(a: BezierSegment, b: BezierSegment): InterceptedBezierPoint {
  val precision = maxOf(
    minOf(a.lengthF, b.lengthF) / 1E4f,
    1E-4f
  )
  return interceptCubicBezier(a, b, precision, 0f, 1f, 0f, 1f)
}

fun nearestBezierPoint(segment: BezierSegment, pt:Pt): Float {
  return 0.5f
//  val precision = maxOf(
//    minOf(a.lengthF, b.lengthF) / 1E4f,
//    1E-4f
//  )
//  return interceptCubicBezier(a, b, precision, 0f, 1f, 0f, 1f)
}

fun interceptCubicBezier(
  a: BezierSegment, b: BezierSegment,
  precision: Float,
  ta1: Float, ta2: Float,
  tb1: Float, tb2: Float,
  recursion:Int = 0
): InterceptedBezierPoint {
  return if (a.subSegment(ta1, ta2).aabb intercepted b.subSegment(tb1, tb2).aabb) {
    val tam = (ta1 + ta2) / 2
    val tbm = (tb1 + tb2) / 2
    val diagonalF = a.subSegment(ta1, ta2).aabb.size.diagonalF
    val MAX_RECURSION = 100
    if (recursion > MAX_RECURSION) {
      println("recursion > MAX_RECURSION")
    }
    if (diagonalF < precision || recursion > MAX_RECURSION) {
      InterceptedBezierPoint(listOf(tam), listOf(tbm))
    } else {
      interceptCubicBezier(a, b, precision, ta1, tam, tb1, tbm, recursion+1) +
        interceptCubicBezier(a, b, precision, ta1, tam, tbm, tb2, recursion+1) +
        interceptCubicBezier(a, b, precision, tam, ta2, tb1, tbm, recursion+1) +
        interceptCubicBezier(a, b, precision, tam, ta2, tbm, tb2, recursion+1)
    }
  } else {
    InterceptedBezierPoint(emptyList(), emptyList())
  }
}

infix fun Rect.intercepted(b: Rect): Boolean {
  val a = this
  val notIntercepter = a.top >= b.bottom || b.top >= a.bottom || a.left >= b.right || b.left >= a.right
  return notIntercepter.not()
}
