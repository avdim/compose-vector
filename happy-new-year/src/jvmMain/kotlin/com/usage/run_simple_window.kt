package com.usage

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import lib.vector.TxtButton

fun runSimpleComposableWindow(content: @Composable () -> Unit): Unit {
  application {
    Window(
      onCloseRequest = ::exitApplication,
//      state = rememberWindowState(width = 800.dp, height = 800.dp)
    ) {
      content()
    }
  }

}

fun runSimpleClickerWindow(content: @Composable (clicksCount: Int) -> Unit): Unit {
  runSimpleComposableWindow {
    var clicksCount by remember { mutableStateOf(0) }
    Column {
      TxtButton("Increment $clicksCount") {
        clicksCount++
      }
      content(clicksCount)
    }
  }
}
