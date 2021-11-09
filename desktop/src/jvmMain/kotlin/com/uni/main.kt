package com.uni

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

const val EDIT_MODE = true

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() = application {
  Window(
    onCloseRequest = ::exitApplication,
    state = rememberWindowState(width = 800.dp, height = 800.dp)
  ) {
    if (EDIT_MODE) {
      EditMode()
    } else {
      DisplayMode()
    }
  }
}

interface GeneratedScope {
  fun drawCurve(points: List<Pt>)
}

inline fun Path.moveTo(x: Int, y: Int) {
  moveTo(x.toFloat(), y.toFloat())
}

inline fun Path.lineTo(x: Int, y: Int) {
  lineTo(x.toFloat(), y.toFloat())
}
