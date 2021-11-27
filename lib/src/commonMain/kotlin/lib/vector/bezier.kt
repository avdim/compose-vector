package lib.vector

import androidx.compose.ui.graphics.Path

data class BezierSegment(val start: Pt, val end: Pt, val refStart: Pt, val refEnd: Pt)

val BezierSegment.p1 get():Pt = start
val BezierSegment.p2 get():Pt = refStart
val BezierSegment.p3 get():Pt = refEnd
val BezierSegment.p4 get():Pt = end

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

fun BezierSegment.points(count: Int): List<Pt> = (0..count).map { it.toFloat() / count }.map { point(it) }

fun BezierSegment.point(t: Float):Pt =
  calcBezier3Pt(t, start, refStart, refEnd, end)

fun calcBezier3Pt(t: Float, p0: Pt, p1: Pt, p2: Pt, p3: Pt): Pt {
  val t2 = t * t
  val t3 = t2 * t
  val mt = 1 - t
  val mt2 = mt * mt
  val mt3 = mt2 * mt
  return p0 * mt3 + 3 * p1 * mt2 * t + 3 * p2 * mt * t2 + p3 * t3
}

fun calcBezier2Pt(t: Float, p0: Pt, p1: Pt, p2: Pt): Pt {
  val t2 = t * t
  val mt = 1 - t
  val mt2 = mt * mt
  return p0 * mt2 + p1 * 2f * mt * t + p2 * t2
}

fun BezierSegment.point2(t: Float): Pt {
  var currentPoints: List<Pt> = listOf(start, refStart, refEnd, end)
  while (currentPoints.size > 1) {
    currentPoints = currentPoints.windowed(2).map { (a, b) -> a * (1 - t) + b * t }
  }
  return currentPoints.first()
}

// p0(1-t)^3 + p1*3*(1-t)^2*t + p2*3(1-t)t^2 + p3*t^3
// производная в точках p0 и p3 совпадают с оригиналом

fun BezierSegment.derivative(t: Float): Pt {
  //3(B-A), 3(C-B), 3(D-C)
  return calcBezier2Pt(t, 3 * (p2 - p1), 3 * (p3 - p2), 3 * (p4 - p3))
}

fun BezierSegment.subSegment(t1:Float, t2:Float):BezierSegment {
  val A = point(t1)
  val D = point(t2)

  fun tArrCalc(t:Float): List<Float> {
    val mt = 1 - t
    return listOf(
      mt * mt * mt,
      3 * mt * mt * t,
      3 * mt * t * t,
      t * t * t,
      t1 * mt + t2 * t
    )
  }
  val arr50 = tArrCalc(0.5f)
  val arr75 = tArrCalc(0.75f)
//  A * arr50[0] + B * arr50[1] + C * arr50[2] + D * arr50[3] = point(arr50[4])
//  A * arr75[0] + B * arr75[1] + C * arr75[2] + D * arr75[3] = point(arr75[4])
  val up = point(arr75[4]) - A * arr75[0] - D * arr75[3] - (point(arr50[4]) - A * arr50[0] - D * arr50[3]) * arr75[1] / arr50[1]
  val down = (arr75[2] -  arr50[2] * arr75[1] / arr50[1])
  val C = up / down
  val B = (point(arr50[4]) - A * arr50[0] - D * arr50[3] - C * arr50[2]) / arr50[1]

  return BezierSegment(
    start = A,
    end = D,
    refStart = B,
    refEnd = C
  )
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

val BezierSegment.aabb: Rect
  get() {
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
    val size = Size(max - min)
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

val BezierSegment.length:Double get() = points(10).windowed(2).sumOf { (a,b) -> a.distance(b) }
val BezierSegment.lengthF:Float get() = length.toFloat()
