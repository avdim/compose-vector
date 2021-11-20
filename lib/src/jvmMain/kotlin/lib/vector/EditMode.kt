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

sealed class DrawOptions {
  class Selection:DrawOptions()
  data class Curve(val color:ULong = Color.Blue.value):DrawOptions()//t
  data class Rect(val color:ULong = Color.Yellow.value):DrawOptions()
  data class Img(val image: ImageBitmap? = null):DrawOptions()
}

data class ControllerState(val name:String, val options:DrawOptions)

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun EditMode(modifier: Modifier, lambda: GeneratedScope.() -> Unit) {
  // Init
  val generatedElements: MutableList<Element> = mutableListOf()
  val generatedScope = object : GeneratedScope {
    override fun drawCurve(color:ULong, points: List<Pt>) {
      generatedElements.add(Element.Curve(color, points))
    }

    override fun drawRect(color:ULong, start: Pt, end: Pt) {
      generatedElements.add(Element.Rect(color, start, end))
    }

    override fun drawBitmap(x:Int, y:Int, byteArray: ByteArray) {
      generatedElements.add(Element.Bitmap(x, y, byteArray))
    }
  }
  generatedScope.lambda()

  // State
  var savedElements by remember { mutableStateOf<List<Element>>(generatedElements) }
  var controllers by remember {
    mutableStateOf(
      listOf(
        ControllerState("Selection", DrawOptions.Selection()),
        ControllerState("Curve", DrawOptions.Curve()),
        ControllerState("Rectangle", DrawOptions.Rect()),
        ControllerState("Image", DrawOptions.Img()),
      )
    )
  }
  var selectedControllerIndex by remember { mutableStateOf(1) }
  val controllerState: ControllerState by derivedStateOf { controllers.get(selectedControllerIndex) }
  fun replaceChangeOptions(lambda:()->DrawOptions) {
    controllers = controllers.toMutableList().also {
      it[selectedControllerIndex] = it[selectedControllerIndex].copy(
        options = lambda()
      )
    }
  }

  // UI
  var editPanelIsOpen by remember { mutableStateOf(true) }
  if (editPanelIsOpen) {
    Window(
      state = WindowState(width = 400.dp, height = 800.dp, position = WindowPosition(0.dp, 0.dp)),
      onCloseRequest = {
        editPanelIsOpen = false
      }) {
      Row {
        Column {
          TextButton("Clear") { savedElements = emptyList() } // todo Are you sure?
          controllers.forEachIndexed { index, it ->
            fun onClick() {
              selectedControllerIndex = index
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
        }
        Column {
          Text("${controllerState.name} settings:")
          val options = controllerState.options
          when (options) {
            is DrawOptions.Curve -> {
              ColorPicker(options.color) {
                replaceChangeOptions { options.copy(color = it) }
              }
            }
            is DrawOptions.Rect -> {
              ColorPicker(options.color) {
                replaceChangeOptions { options.copy(color = it) }
              }
            }
            is DrawOptions.Img -> {
              TextButton("get clipboard image") {
                val img: BufferedImage? = getClipboardImage()
                if (img != null) {
                  replaceChangeOptions { options.copy(image = img.toComposeImageBitmap()) }
                }
              }
              options.image?.let {
                Image(
                  BitmapPainter(it),
                  contentDescription = null,
                  modifier = Modifier.size(it.width.dp, it.height.dp),
                  contentScale = ContentScale.Crop
                )
              }
            }
          }
        }
      }
    }
  }

  var currentPoints by remember { mutableStateOf<List<Pt>?>(null) }
  val currentElement: Element? by derivedStateOf {
    val options = controllerState.options
    when (options) {
      is DrawOptions.Selection -> {
        null
      }
      is DrawOptions.Curve -> {
        Element.Curve(options.color, points = currentPoints.orEmpty())
      }
      is DrawOptions.Rect -> {
        val points = currentPoints
        if (points != null && points.size >= 2) {
          Element.Rect(options.color, points.first(), points.last())
        } else {
          null
        }
      }
      is DrawOptions.Img -> {
        val img = options.image
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

  fun pointerStart(pt: Pt) {}
  fun pointerMove(pt: Pt) {}
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
        is Element.Curve -> drawCurve(e.color, e.points)
        is Element.Rect -> drawRect(e.color, e.start, e.end)
        is Element.Bitmap -> drawBitmap(e.x, e.y, e.byteArray)
      }
    }
  }
  Row() {
    TextButton("Edit") {
      editPanelIsOpen = !editPanelIsOpen
    }
    TextButton("Copy result to clipboard") {
      val result: String = generateCode(savedElements)
      pasteToClipboard(result)
    }
  }

}

val Point.pt get() = Pt(x, y)

@Composable
fun ColorPicker(currentColor:ULong, onChageColor:(ULong)->Unit) {
  Column {
    TextButton("Blue") {
      onChageColor(Color.Blue.value)
    }
    TextButton("Red") {
      onChageColor(Color.Red.value)
    }
    TextButton("Yellow") {
      onChageColor(Color.Yellow.value)
    }
  }
}
