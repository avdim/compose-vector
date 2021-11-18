package lib.vector

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.intellij.getClipboardImage
import lib.vector.utils.toByteArray
import java.awt.Point
import java.awt.image.BufferedImage

enum class ControllerState() {
  IDLE,
  CURVE,
  RECT,
  IMG;
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun EditMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit) {
  // Init
  val generatedElements: MutableList<Element> = mutableListOf()
  val generatedScope = object : GeneratedScope {
    override fun drawCurve(points: List<Pt>) {
      generatedElements.add(Element.Curve(points))
    }

    override fun drawRect(start: Pt, end: Pt) {
      generatedElements.add(Element.Rect(start, end))
    }

    override fun drawBitmap(x:Int, y:Int, byteArray: ByteArray) {
      generatedElements.add(Element.Bitmap(x, y, byteArray))
    }
  }
  generatedScope.lambda()

  // State
  var savedElements by remember { mutableStateOf<List<Element>>(generatedElements) }
  var controllerState: ControllerState by remember { mutableStateOf(ControllerState.CURVE) }
  var image: ImageBitmap? by remember { mutableStateOf<ImageBitmap?>(null) }//TODO delete

  // UI
  var editPanelIsOpen by remember { mutableStateOf(false) }
  if (editPanelIsOpen) {
    Window(
      state = WindowState(width = 400.dp, height = 800.dp, position = WindowPosition(0.dp, 0.dp)),
      onCloseRequest = {
        editPanelIsOpen = false
      }) {
      Column {
        TextButton("Clear all") { savedElements = emptyList() }
        ControllerState.values().forEach {
          fun onClick() {
            controllerState = it
          }
          Row(Modifier.clickable {
            onClick()
          }) {
            RadioButton(controllerState == it, onClick = {
              onClick()
            })
            Text(it.name)
          }
        }

        TextButton("get clipboard image") {
          val img: BufferedImage? = try {
            getClipboardImage()
          } catch (t: Throwable) {
            println("exception in getClipboardImage")
            null
          }
          println(img)
          if (img != null) {
            image = img.toComposeImageBitmap()
          }
        }

        val img = image
        if (img != null) {
          Image(
            BitmapPainter(img),
            contentDescription = null,
            modifier = Modifier.size(img.width.dp, img.height.dp),
            contentScale = ContentScale.Crop
          )
        }
      }
    }
  }

  var currentPoints by remember { mutableStateOf<List<Pt>?>(null) }
  val currentElement: Element? by derivedStateOf {
    when (controllerState) {
      ControllerState.IDLE -> {
        null
      }
      ControllerState.CURVE -> {
        Element.Curve(points = currentPoints.orEmpty())
      }
      ControllerState.RECT -> {
        val points = currentPoints
        if (points != null && points.size >= 2) {
          Element.Rect(points.first(), points.last())
        } else {
          null
        }
      }
      ControllerState.IMG -> {
        val img = image
        val points = currentPoints

        if(img != null && points != null && points.isNotEmpty()) {
          Element.Bitmap(
            x = points.last().x,
            y = points.last().y,
            byteArray = img.toByteArray()
          )
        } else {
          null
        }
      }
    }
  }

  fun pointerStart(pt: Pt) {

  }

  fun pointerMove(pt: Pt) {

  }

  fun pointerEnd(pt: Pt?) {
    val e = currentElement
    if (e != null) {
      savedElements = savedElements + e
    }
  }

  DisplayMode(
    modifier = modifier.pointerInput(Unit) {
      while (true) {
        val event = awaitPointerEventScope { awaitPointerEvent() }
        val point = event.mouseEvent?.point
        if (event.buttons.isPrimaryPressed) {
          val previousPoints = currentPoints
          if (point != null) {
            if (previousPoints == null) {
              currentPoints = listOf(point.pt)
              pointerStart(point.pt)
            } else {
              currentPoints = previousPoints + point.pt
              pointerMove(point.pt)
            }
          }
        } else {
          if (currentPoints != null) {
            pointerEnd(point?.pt)
            currentPoints = null
          }
        }
      }
    }
  ) {
    (savedElements + listOfNotNull(currentElement)).forEach { e ->
      when (e) {
        is Element.Curve -> {
          drawCurve(e.points)
        }
        is Element.Rect -> {
          drawRect(e.start, e.end)
        }
        is Element.Bitmap -> {
          drawBitmap(e.x, e.y, e.byteArray)
        }
      }
    }
  }
  Row() {
    TextButton("copy to clipboard") {
      val result: String = generateCode(savedElements)
      pasteToClipboard(result)
    }
    TextButton("Edit") {
      editPanelIsOpen = !editPanelIsOpen
    }
  }

}

val Point.pt get() = Pt(x, y)

