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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import lib.vector.utils.lineTo
import lib.vector.utils.moveTo
import lib.vector.utils.toImageBitmap
import kotlin.math.absoluteValue

private val Pt.size: Size get() = Size(x.absoluteValue.toFloat(), y.absoluteValue.toFloat())
val Pt.offset: Offset get() = Offset(x.toFloat(), y.toFloat())
infix operator fun Pt.minus(other: Pt): Pt = Pt(x - other.x, y - other.y)

@Composable
fun DisplayMode(modifier:Modifier, lambda: GeneratedScope.() -> Unit) {
  Canvas(
    modifier.wrapContentSize(Alignment.Center)
      .fillMaxSize()
  ) {
    val generatedScope = object : GeneratedScope {
      override fun drawCurve(color:ULong, points: List<Pt>) {
        if (points.isNotEmpty()) {
          drawPath(
            path = Path().apply {
              val start = points[0]
              moveTo(start.x, start.y)
              (points.drop(1)).forEach {
                lineTo(it.x, it.y)
              }
            },
            Color(color),
            style = Stroke(width = 2f)
          )
        }
      }

      override fun drawRect(color:ULong, start: Pt, end: Pt) {
        drawRect(color = Color(color), topLeft = Offset(minOf(start.x, end.x).toFloat(), minOf(start.y, end.y).toFloat()), size = (end - start).size)
      }

      override fun drawBitmap(x:Int, y:Int, byteArray: ByteArray) {
        drawImage(image = byteArray.toImageBitmap(), topLeft = Offset(x.toFloat(), y.toFloat()))
      }
    }
    generatedScope.lambda()
  }
}
