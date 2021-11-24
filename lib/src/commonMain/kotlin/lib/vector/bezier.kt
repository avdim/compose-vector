package lib.vector

class BezierReference(val refFrom: Pt, val refTo: Pt)

fun calcDefaultBezierReferences(before: Pt, from: Pt, to: Pt, after: Pt): BezierReference {
  return BezierReference(
    refFrom = calcDefaultBezierInTriangle(before, from, to).refFrom,
    refTo = calcDefaultBezierInTriangle(from, to, after).refTo
  )
}

class InTriangleResult(val refFrom: Pt, val refTo: Pt)

fun calcDefaultBezierInTriangle(a: Pt, b: Pt, c: Pt): InTriangleResult {
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

