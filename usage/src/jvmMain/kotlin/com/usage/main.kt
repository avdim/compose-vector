package com.usage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.usage.UsageInCommon

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() {
  application {
    Window(
      onCloseRequest = ::exitApplication,
      state = rememberWindowState(width = 800.dp, height = 800.dp)
    ) {
      UsageInCommon()
    }
  }
}
