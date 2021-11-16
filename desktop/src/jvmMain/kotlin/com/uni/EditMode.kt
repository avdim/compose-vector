package com.uni

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

@Composable
fun EditMode(lambda: GeneratedScope.() -> Unit) {
  // Init
  val generatedCurves: MutableList<Curve> = mutableListOf()
  val generatedScope = object : GeneratedScope {
    override fun drawCurve(points: List<Pt>) {
      generatedCurves.add(Curve(points))
    }
  }
  generatedScope.lambda()

  // State
  var curves by remember { mutableStateOf<List<Curve>>(generatedCurves) }
  var currentCurve by remember { mutableStateOf<Curve?>(null) }

  // UI
  var editPanelIsOpen by remember { mutableStateOf(false) }
  if (editPanelIsOpen) {
    Window(
      state = WindowState(width = 200.dp, height = 800.dp, position = WindowPosition(0.dp, 0.dp)),
      onCloseRequest = {
        editPanelIsOpen = false
      }) {
      Column {
        TextButton("Clear all") { curves = emptyList() }
      }
    }
  }

  Canvas(
    Modifier.wrapContentSize(Alignment.Center)
      .fillMaxSize()
      .pointerInput(Unit) {
        while (true) {
          val event = awaitPointerEventScope { awaitPointerEvent() }
          val awtEvent = event.mouseEvent
          if (event.buttons.isPrimaryPressed) {
            val point = awtEvent?.point
            val current = currentCurve ?: (Curve(listOf()).also { currentCurve = it })
            if (point != null) {
              currentCurve = current.copy(points = current.points + Pt(point.x, point.y))
            }
          } else {
            currentCurve?.let {
              curves = curves + it
            }
            currentCurve = null
          }
        }
      }
  ) {
    drawPath(
      path = Path().apply {
        (curves + listOfNotNull(currentCurve)).forEach {
          val points = it.points
          val start = points[0]
          moveTo(start.x, start.y)
          (points.drop(1)).forEach {
            lineTo(it.x, it.y)
          }
        }
      },
      Color.Blue,
      style = Stroke(width = 2f)
    )
  }
  Row() {
    TextButton("copy to clipboard") {
      val result: String = generateCode(curves)
      pasteToClipboard(result)
    }
    TextButton("Edit") {
      editPanelIsOpen = !editPanelIsOpen
    }
  }

}


