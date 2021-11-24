package lib.vector

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.absoluteValue

val Pt.size: Size get() = Size(x.absoluteValue, y.absoluteValue)
val Pt.offset: Offset get() = Offset(x, y)
infix operator fun Pt.minus(other: Pt): Pt = Pt(x - other.x, y - other.y)
infix operator fun Pt.plus(other: Pt): Pt = Pt(x + other.x, y + other.y)
infix operator fun Pt.times(scale: Float): Pt = Pt(x * scale, y * scale)
infix operator fun Pt.div(divider: Float): Pt = this * (1f / divider)

