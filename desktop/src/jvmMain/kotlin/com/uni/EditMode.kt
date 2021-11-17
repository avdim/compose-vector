package com.uni

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import com.intellij.getClipboardImage
import com.uni.serializable.Element
import com.uni.serializable.Pt
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

enum class ControllerState() {
  IDLE,
  CURVE,
  RECT;
}

@Composable
fun EditMode(lambda: GeneratedScope.() -> Unit) {
  // Init
  val generatedElements: MutableList<Element> = mutableListOf()
  val generatedScope = object : GeneratedScope {
    override fun drawCurve(points: List<Pt>) {
      generatedElements.add(Element.Curve(points))
    }

    override fun drawRect(start: Pt, end: Pt) {
      generatedElements.add(Element.Rect(start, end))
    }
  }
  generatedScope.lambda()

  // State
  var savedElements by remember { mutableStateOf<List<Element>>(generatedElements) }
  var controllerState: ControllerState by remember { mutableStateOf(ControllerState.IDLE) }

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

        var image: ImageBitmap? by remember { mutableStateOf<ImageBitmap?>(null) }//TODO delete
        TextButton("get clipboard image") {
          val img: BufferedImage? = try {
            getClipboardImage()
          } catch (t: Throwable) {
            println("exception in getClipboardImage")
            null
          }
          println(img)
          if (img != null) {
            val result: ImageBitmap = org.jetbrains.skia.Image.makeFromEncoded(toByteArray(img)).toComposeImageBitmap()
            image = result
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
  val currentElement by derivedStateOf {
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
    modifier = Modifier.pointerInput(Unit) {
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


fun toByteArray(bitmap: BufferedImage): ByteArray {
  val baos = ByteArrayOutputStream()
  ImageIO.write(bitmap, "png", baos)
  return baos.toByteArray()
}

