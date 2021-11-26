package lib.vector

import androidx.compose.ui.graphics.Path

class BezierSegment(val start: Pt, val end: Pt, val refStart: Pt, val refEnd: Pt)

fun LineSegment<Pt>.bezierSegment(startRef: Pt?, endRef: Pt?): BezierSegment {
  val defaultBezierReferences by lazy(mode = LazyThreadSafetyMode.NONE) {
    calcDefaultBezierReferences(before = before, start = start, end = end, after = after)
  }
  return BezierSegment(
    start = start, end = end, refStart = startRef ?: defaultBezierReferences.refStart, refEnd = endRef ?: defaultBezierReferences.refEnd
  )
}

private class InTriangleResult(val refFrom: Pt, val refTo: Pt)

private fun calcDefaultBezierInTriangle(a: Pt, b: Pt, c: Pt): InTriangleResult {
  val ab = a.distance(b).toFloat()
  val bc = b.distance(c).toFloat()
  val scaleA = ab / (ab + bc + 0.001f)
  val scaleC = bc / (ab + bc + 0.001f)
  val direction = (c - a)
  return InTriangleResult(
    refFrom = b + direction * scaleA * DEFAULT_BEZIER_SCALE,
    refTo = b - direction * scaleC * DEFAULT_BEZIER_SCALE,
  )
}

private class BezierReferences(val refStart: Pt, val refEnd: Pt)

private fun calcDefaultBezierReferences(before: Pt, start: Pt, end: Pt, after: Pt): BezierReferences {
  return BezierReferences(
    refStart = calcDefaultBezierInTriangle(before, start, end).refFrom, refEnd = calcDefaultBezierInTriangle(start, end, after).refTo
  )
}

fun BezierSegment.points(count: Int): List<Pt> = (0..count).map { it.toFloat() / count }.map {
  calcBezier3Pt(it, start, refStart, refEnd, end)
}

fun calcBezier3Pt(t: Float, p0: Pt, p1: Pt, p2: Pt, p3: Pt): Pt {
  val t2 = t * t
  val t3 = t2 * t
  val mt = 1 - t
  val mt2 = mt * mt
  val mt3 = mt2 * mt
  return p0 * mt3 + 3 * p1 * mt2 * t + 3 * p2 * mt * t2 + p3 * t3
}

fun BezierSegment.split(t: Float): Pair<BezierSegment, BezierSegment> {
  // https://pomax.github.io/bezierinfo/index.html#splitting

  val left = mutableListOf<Pt>()
  val right = mutableListOf<Pt>()

  fun drawCurvePoint(points: List<Pt>, t: Float) {
    if (points.size == 1) {
      left.add(points.first())
      right.add(points.last())
//      draw(points[0])
    } else if (points.size > 1) {
      left.add(points.first())
      right.add(points.last())
      val newpoints = (0 until (points.size - 1)).map { i ->
        (1 - t) * points[i] + t * points[i + 1]
      }
      drawCurvePoint(newpoints, t)
    }
  }
  drawCurvePoint(listOf(start, refStart, refEnd, end), t)
  fun MutableList<Pt>.toSegment() =
    BezierSegment(start = get(0), end = get(3), refStart = get(1), refEnd = get(2))

  return left.toSegment() to right.toSegment()
}

fun BezierSegment.aabb(): Rect {
  val p1 = start
  val p2 = refStart
  val p3 = refEnd
  val p4 = end
  val a = 3 * (3 * p2 - p1 - 3 * p3 + p4)
  val b = 6 * (p1 - 2 * p2 + p3)
  val c = 3 * (p2 - p1)
  val xExtremum = listOf(start.x, end.x) + solveRoots(a.x, b.x, c.x).filter { it > 0 && it < 1 }.map { calcBezier3Pt(it, p1, p2, p3, p4).x }
  val yExtremum = listOf(start.y, end.y) + solveRoots(a.y, b.y, c.y).filter { it > 0 && it < 1 }.map { calcBezier3Pt(it, p1, p2, p3, p4).y }
  val min = Pt(xExtremum.minOrNull()!!, yExtremum.minOrNull()!!)
  val max = Pt(xExtremum.maxOrNull()!!, yExtremum.maxOrNull()!!)
  val size = max - min
  return Rect(
    topLeft = min,
    size = size,
  )
}

fun BezierSegment.toPath(): Path =
  Path().apply {
    val start = start
    moveTo(start.x, start.y)
    val result = this@toPath
    with(result) {
      cubicTo(refStart.x, refStart.y, refEnd.x, refEnd.y, end.x, end.y)
//                lineTo(to.x, to.y)
    }
  }
