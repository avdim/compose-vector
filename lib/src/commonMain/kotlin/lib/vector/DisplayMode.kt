package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import lib.vector.utils.toImageBitmap
import kotlin.math.absoluteValue

private val Pt.size: Size get() = Size(x.absoluteValue.toFloat(), y.absoluteValue.toFloat())
val Pt.offset: Offset get() = Offset(x.toFloat(), y.toFloat())
infix operator fun Pt.minus(other: Pt): Pt = Pt(x - other.x, y - other.y)
infix operator fun Pt.plus(other: Pt): Pt = Pt(x + other.x, y + other.y)
infix operator fun Pt.times(scale: Float): Pt = Pt(x * scale, y * scale)

@Composable
fun DisplayMode(modifier:Modifier, lambda: GeneratedScope.() -> Unit) {
  Canvas(modifier.wrapContentSize(Alignment.Center).fillMaxSize()) {
    val generatedScope = object : GeneratedScope {
      override fun mkPt(x: Float, y: Float): MakePt = MakePt { _, _ -> Pt(x, y) }

      override fun drawCurve(color:ULong, points: List<Pt>, bezierRef:Map<Pt, BezierRef>) {
        if (points.isNotEmpty()) {
          drawPath(
            path = Path().apply {
              val start = points[0]
              moveTo(start.x, start.y)
              (points.take(1) + points + points.takeLast(1)).windowed(4).forEach { (before, a, b, after) ->
                val savedBezierA = bezierRef[a]?.refA
                val savedBezierB = bezierRef[b]?.refB
                val result = calcBezier(before, a, b, after)
                val bezierA: Pt = savedBezierA ?: result.bezierA
                val bezierB: Pt = savedBezierB ?: result.bezierB

                drawCircle(Color.Red, 2f, bezierA.offset)
                drawCircle(Color.Green, 2f, bezierB.offset)
                cubicTo(bezierA.x, bezierA.y, bezierB.x, bezierB.y, b.x, b.y)
//                lineTo(b.x, b.y)
              }
            },
            Color(color),
            style = Stroke(width = 2f)
          )
        }
      }

      override fun drawRect(color:ULong, start: Pt, end: Pt) {
        rotate(degrees = 45f, pivot = start.offset) {
          drawRect(color = Color(color), topLeft = Offset(minOf(start.x, end.x).toFloat(), minOf(start.y, end.y).toFloat()), size = (end - start).size)
        }
      }

      override fun drawBitmap(pt:Pt, byteArray: ByteArray) {
        scale(2f, pivot = pt.offset) {
          drawImage(image = byteArray.toImageBitmap(), topLeft = pt.offset)
        }
      }
    }
    generatedScope.lambda()
  }
}

inline val Int.f get() = toFloat()

class BezierResult(val bezierA: Pt, val bezierB: Pt)

fun calcBezier(before: Pt, a: Pt, b: Pt, after: Pt): BezierResult {
  return BezierResult(
    bezierA = (a + (a - before) * 0.4f),
    bezierB = (b - (after - b) * 0.3f)
  )
}
