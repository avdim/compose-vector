package lib.vector

import lib.vector.utils.fromBase64
import kotlin.math.absoluteValue
import kotlin.math.sqrt

interface Pt {
  val x: Float
  val y: Float
}

fun Pt(x: Float, y: Float) = PtDisplay(x, y)
fun Pt(x: Int, y: Int) = PtDisplay(x.toFloat(), y.toFloat())
data class PtDisplay(override val x: Float, override val y: Float) : Pt

data class Id(val value: Long, val name: String? = null)
data class PtEdit(override val x: Float, override val y: Float, val id: Id) : Pt

infix fun Pt.distance(other: Pt): Double {
  val dx = (other.x - x).absoluteValue.toDouble()
  val dy = (other.y - y).absoluteValue.toDouble()
  return sqrt(dx * dx + dy * dy)
}

data class BezierRef(
  val refA: Pt?,
  val refB: Pt?
)

data class BezierRefEdit(
  val refA: Id?,
  val refB: Id?
)

sealed class Element {
  data class Curve(val color: ULong, val points: List<Id>, val bezierRef: Map<Id, BezierRefEdit>) : Element()
  data class Rect(val color: ULong, val start: Id, val end: Id) : Element()
  data class Bitmap(val topLeft: Id, val byteArray: ByteArray) : Element() {
    constructor(topLeft: Id, base64Str: String) : this(topLeft, base64Str.fromBase64())
  }
}
