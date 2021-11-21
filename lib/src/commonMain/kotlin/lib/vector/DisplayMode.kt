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
import lib.vector.utils.lineTo
import lib.vector.utils.moveTo
import lib.vector.utils.toImageBitmap
import kotlin.math.absoluteValue

private val Pt.size: Size get() = Size(x.absoluteValue.toFloat(), y.absoluteValue.toFloat())
val Pt.offset: Offset get() = Offset(x.toFloat(), y.toFloat())
infix operator fun Pt.minus(other: Pt): Pt = Pt(x - other.x, y - other.y)
infix operator fun Pt.plus(other: Pt): Pt = Pt(x + other.x, y + other.y)
infix operator fun Pt.times(scale: Float): Pt = Pt((x * scale).toInt(), (y * scale).toInt())

@Composable
fun DisplayMode(modifier:Modifier, lambda: GeneratedScope.() -> Unit) {
  Canvas(modifier.wrapContentSize(Alignment.Center).fillMaxSize()) {
    val generatedScope = object : GeneratedScope {
      override fun mkPt(x: Int, y: Int): MakePt = MakePt { _, _ -> Pt(x, y) }

      override fun drawCurve(color:ULong, points: List<Pt>) {
        if (points.isNotEmpty()) {
          drawPath(
            path = Path().apply {
              val start = points[0]
              moveTo(start.x, start.y)
              (points.take(1) + points + points.takeLast(1)).windowed(4).forEach { (before, a, b, after) ->
                val ra:Pt = a + (a - before) * 0.4f
                val rb:Pt = b - (after - b) * 0.3f
                // line before -> b move to center point a and scale 0.5f
                // 1 (x.x - before.x) / (b.x - before.x) = (x.y - before.y) / (b.y - before.y)
                // 2 (x.x - a.x) * (b.x - before.x) + (x.y - a.y) * (b.y - before.y) = 0
                // 2 x.x = a.x -(x.y - a.y) * C1  // C1 = (b.y - before.y) / (b.x - before.x)
                // 2 x.x = a.x -x.y*C1 + a.y * C1
                // 1 (a.x -x.y*C1 + a.y * C1 - a.x) * (b.x - before.x) + (x.y - a.y) * C2 = 0 //C2  =(b.y - before.y)
                // 1 (a.x -x.y*C1 + a.y * C1 - a.x) * (b.x - before.x) + x.y * C2 - a.y * C2 = 0
                // 1 (C3 - x.y*C1) * (b.x - before.x) + x.y * C2 - a.y * C2 = 0// C3 = a.x + a.y * C1 - a.x
                // 1 (x.y*C1 - C3) * (before.x - b.x) + x.y * C2 - a.y * C2 = 0
                // 1 x.y * (C2 +  C1 * (before.x - b.x)) = C3 * (before.x - b.x) + a.y * C2
                // 1 x.y = C3 * (before.x - b.x) + a.y * C2 / (C2 +  C1 * (before.x - b.x))
                // 2 x.x = a.x -x.y*C1 + a.y * C1
                //прямая ra, a, rb
                val C1 = (b.y - before.y) / (b.x - before.x)
                drawCircle(Color.Red, 2f, ra.offset)
                drawCircle(Color.Green, 2f, rb.offset)
//                cubicTo(ra.x.f, ra.y.f, rb.x.f, rb.y.f, b.x.f, b.y.f)
                lineTo(b.x, b.y)
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
