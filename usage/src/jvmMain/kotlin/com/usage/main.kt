package com.usage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.usage.UsageInCommon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import lib.vector.globalKeyListener

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
fun main() {
  application {
    Window(
      onCloseRequest = ::exitApplication,
      state = rememberWindowState(width = 800.dp, height = 750.dp),
      onKeyEvent = {
        GlobalScope.launch {
          globalKeyListener.emit(it.key)
        }
        false
      }
    ) {
      UsageInCommon()
    }
  }
}
