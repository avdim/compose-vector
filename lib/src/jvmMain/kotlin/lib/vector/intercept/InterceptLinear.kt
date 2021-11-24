package lib.vector.intercept

import lib.vector.BezierSegment
import lib.vector.Pt

fun interceptLinear(a: BezierSegment, b: BezierSegment): Pt? {
  val a1 = a.start
  val a2 = a.end
  val b1 = b.start
  val b2 = b.end
  fun intercept(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, x4: Float, y4: Float): Pt? {
    //https://habr.com/ru/post/523440/
    val n: Float;
    if (y2 - y1 != 0f) {  // a(y)
      val q = (x2 - x1) / (y1 - y2);
      val sn = (x3 - x4) + (y3 - y4) * q;
      if (sn == 0f) {
        return null; // c(x) + c(y)*q
      }
      val fn = (x3 - x1) + (y3 - y1) * q;   // b(x) + b(y)*q
      n = fn / sn;
    } else {
      if (y3 - y4 == 0f) {
        return null;   // b(y)
      }
      n = (y3 - y1) / (y3 - y4);   // c(y)/b(y)
    }
    val x = x3 + (x4 - x3) * n;  // x3 + (-b(x))*n
    val y = y3 + (y4 - y3) * n;  // y3 +(-b(y))*n
    val r = Pt(x, y)
    if (true
      && r.x > minOf(x1, x2) && r.x > minOf(x3, x4)
      && r.x < maxOf(x1, x2) && r.x < maxOf(x3, x4)
      && r.y > minOf(y1, y2) && r.y > minOf(y3, y4)
      && r.y < maxOf(y1, y2) && r.y < maxOf(y3, y4)
    ) {
      return r
    } else {
      return null
    }
  }

  val interceptedPoint = intercept(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y)
  return interceptedPoint
}
