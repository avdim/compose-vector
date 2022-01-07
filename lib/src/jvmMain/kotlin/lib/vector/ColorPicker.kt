package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState

@Composable
fun ColorPicker(currentColor: ULong, onChangeColor: (ULong) -> Unit) {
  var dialogOpen by remember { mutableStateOf(false) }
    Row(modifier = Modifier.clickable {
        dialogOpen = !dialogOpen
    }) {
        Canvas(modifier = Modifier.size(30.dp)) {
            drawRect(color = Color(currentColor))
        }
        Text("color")
    }
  if (dialogOpen) {
      Window(
          state = WindowState(width = 400.dp, height = 800.dp, position = WindowPosition(0.dp, 0.dp)),
          onCloseRequest = {
              dialogOpen = false
          }) {
          Column {
              TxtButton("Blue") {
                  onChangeColor(Color.Blue.value)
              }
              TxtButton("Red") {
                  onChangeColor(Color.Red.value)
              }
              TxtButton("Yellow") {
                  onChangeColor(Color.Yellow.value)
              }
          }
      }
  }
}
