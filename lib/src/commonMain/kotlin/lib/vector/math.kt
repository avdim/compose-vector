package lib.vector

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.absoluteValue
import kotlin.math.sqrt

val Pt.size: Size get() = Size(x.absoluteValue, y.absoluteValue)
val Pt.offset: Offset get() = Offset(x, y)
infix operator fun Pt.minus(other: Pt): Pt = Pt(x - other.x, y - other.y)
infix operator fun Pt.plus(other: Pt): Pt = Pt(x + other.x, y + other.y)
infix operator fun Pt.times(scale: Float): Pt = Pt(x * scale, y * scale)
infix operator fun Float.times(pt: Pt): Pt = pt.times(this)
infix operator fun Int.times(pt: Pt): Pt = pt.times(this.toFloat())
infix operator fun Pt.div(divider: Float): Pt = this * (1f / divider)

fun solveRoots(a: Float, b: Float, c: Float): List<Float> =
  if (a == 0f) {
    //bx+c = 0
    if (b != 0f) {
      listOf(-c / b)
    } else {
      emptyList()
    }
  } else {
    val D = b * b - 4 * a * c
    if (D == 0f) {
      listOf(-b / (2 * a))
    } else if (D > 0) {
      val top1 = -b + sqrt(D)
      val top2 = -b - sqrt(D)
      listOf(top1 / (2 * a), top2 / (2 * a))
    } else {
      emptyList()
    }
  }
