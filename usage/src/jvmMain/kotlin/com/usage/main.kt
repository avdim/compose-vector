package com.usage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.GlobalScope
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
      Box(Modifier.fillMaxSize()) {
//        val viewConfiguration = object : ViewConfiguration {
//          override val longPressTimeoutMillis: Long = 500
//          override val doubleTapTimeoutMillis: Long = 300
//          override val doubleTapMinTimeMillis: Long = 40
//          override val touchSlop: Float get() = 0.0f
//        }
//
//        CompositionLocalProvider(
//          LocalViewConfiguration provides viewConfiguration
//        ) {
          UsageInCommon()
//        }
      }
    }
  }
}
