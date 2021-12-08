package com.usage

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun runSimpleComposableWindow(content: @Composable ()->Unit):Unit {
  application {
    Window(
      onCloseRequest = ::exitApplication,
//      state = rememberWindowState(width = 800.dp, height = 800.dp)
    ) {
      content()
    }
  }

}
