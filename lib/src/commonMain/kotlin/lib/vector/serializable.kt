package lib.vector

import lib.vector.utils.fromBase64
import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.sqrt

interface Pt {
  val x: Float
  val y: Float
}

fun Pt(x: Float, y: Float) = PtDisplay(x, y)
fun Pt(x: Int, y: Int) = PtDisplay(x.toFloat(), y.toFloat())
data class PtDisplay(override val x: Float, override val y: Float) : Pt

@JvmInline
value class Size(val pt: Pt)

val Size.diagonal: Double get() = pt.distance(Pt(0, 0))
val Size.diagonalF: Float get() = pt.distanceF(Pt(0, 0))

data class Rect(val topLeft: Pt, val size: Size)

val Rect.top get() = topLeft.y
val Rect.bottom get() = topLeft.y + size.pt.y
val Rect.left get() = topLeft.x
val Rect.right get() = topLeft.x + size.pt.x

data class Id(val value: Long, val name: String? = null)
data class PtEdit(override val x: Float, override val y: Float, val id: Id) : Pt

infix fun Pt.distance(other: Pt): Double {
  val dx = (other.x - x).absoluteValue.toDouble()
  val dy = (other.y - y).absoluteValue.toDouble()
  return sqrt(dx * dx + dy * dy)
}

infix fun Pt.distanceF(other: Pt): Float {
  val dx = (other.x - x).absoluteValue
  val dy = (other.y - y).absoluteValue
  return sqrt(dx * dx + dy * dy)
}

fun BR(startRef: Pt? = null, endRef: Pt? = null) = BezierRef(startRef, endRef)
data class BezierRef(
  val startRef: Pt? = null,
  val endRef: Pt? = null
)

data class BezierRefEdit(
  val startRef: Id?,
  val endRef: Id?
)

sealed class Element {
  data class Curve(val color: ULong, val points: List<Id>, val bezierRef: Map<Id, BezierRefEdit>) : Element()
  data class Rectangle(val color: ULong, val start: Id, val end: Id) : Element()
  data class Bitmap(val topLeft: Id, val byteArray: ByteArray) : Element() {
    constructor(topLeft: Id, base64Str: String) : this(topLeft, base64Str.fromBase64())
  }
}
