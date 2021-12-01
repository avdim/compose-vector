package com.usage

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import lib.vector.TxtButton

fun main() {
  application {
    Window(
      onCloseRequest = ::exitApplication,
      state = rememberWindowState(width = 800.dp, height = 800.dp)
    ) {
      Column {
        println("recompose main")
        var counter by remember { mutableStateOf(0) }
        NotRecomposed(counter / 5) {
          println("lambda")
        }
        TxtButton("Increment $counter") {
          counter++
        }
      }
    }
  }
}

@Composable
fun NotRecomposed(arg1:Int, lambda: () -> Unit) {
  println("recompose NotRecomposed, arg1: $arg1")
  lambda()
  Text("NotRecomposed")
}
