package com.uni

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.event.MouseEvent

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() = application {
  Window(
    onCloseRequest = ::exitApplication,
    title = "Compose for Desktop",
    state = rememberWindowState(width = 800.dp, height = 800.dp)
  ) {
    var curves by remember { mutableStateOf(listOf<Curve>()) }
    var currentCurve by remember { mutableStateOf<Curve?>(null) }

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
                val o = Offset(point.x.toFloat(), point.y.toFloat())
                currentCurve = current.copy(points = current.points + o)
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
      (curves + listOfNotNull(currentCurve)).forEach {
        if(it.points.isNotEmpty()) {
          drawPath(
            path = Path().apply {
              val start = it.points[0]
              moveTo(start.x, start.y)
              (it.points.drop(1)).forEach {
                lineTo(it.x, it.y)
              }
            },
            it.color,
            style = Stroke(width = 2f)
          )
        }
      }
    }
  }
}

data class Curve(
  val points: List<Offset>,
  val color: Color = listOf(Color.Red, Color.Blue, Color.Green).random(),
)
