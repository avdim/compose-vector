package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.*
import lib.vector.utils.toImageBitmap

const val DEFAULT_BEZIER_SCALE = 0.5f
const val FILL_PATH = false

@Composable
fun DisplayMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit) {
  Canvas(modifier.wrapContentSize(Alignment.Center).fillMaxSize()) {
    val generatedScope = object : GeneratedScope {
      override fun mkPt(x: Float, y: Float): MakePt = MakePt { _, _ -> Pt(x, y) }

      override fun drawCurve(color: ULong, points: List<Pt>, bezierRef: Map<Pt, BezierRef>) {
        if (points.isNotEmpty()) {
          drawPath(
            path = Path().apply {
              val start = points[0]
              moveTo(start.x, start.y)
              points.toLineSegments().forEach { s ->
                val result = s.bezierSegment(bezierRef[s.start]?.startRef, bezierRef[s.end]?.endRef)
//                val subSegment = result.subSegment(0.4f, 0.6f)
//                drawCircle(Color.Red, 4f, subSegment.start.offset)
//                drawCircle(Color.Green, 4f, subSegment.end.offset)
                with(result) {
                  cubicTo(refStart.x, refStart.y, refEnd.x, refEnd.y, end.x, end.y)
//                lineTo(to.x, to.y)
                }
              }
            },
            color = Color(color),
            style = if (FILL_PATH) Fill else Stroke(width = 2f)
          )
        }
      }

      override fun drawRect(color: ULong, start: Pt, end: Pt) {
        rotate(degrees = 0f, pivot = start.offset) {
          drawRect(
            color = Color(color),
            topLeft = Offset(minOf(start.x, end.x), minOf(start.y, end.y)),
            size = (end - start).size
          )
        }
      }

      override fun drawBitmap(pt: Pt, byteArray: ByteArray) {
        scale(1f, pivot = pt.offset) {
          drawImage(image = byteArray.toImageBitmap(), topLeft = pt.offset)
        }
      }
    }
    generatedScope.lambda()
  }
}
