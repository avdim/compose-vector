package com.uni

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.uni.serializable.Pt

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() {
  application {
    Window(
      onCloseRequest = ::exitApplication,
      state = rememberWindowState(width = 800.dp, height = 800.dp)
    ) {
      GeneratedLayer(true) {
        generatedCode()
      }
    }
  }
}

//TODO expect/actual
@Composable
fun GeneratedLayer(editable: Boolean = false, lambda: GeneratedScope.() -> Unit) {
  if (editable) {
    EditMode(lambda = lambda)
  } else {
    DisplayMode(lambda = lambda)
  }
}
