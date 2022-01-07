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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*

@Composable
fun ColorPicker(currentColor: ULong, onChangeColor: (ULong) -> Unit) {
  var dialogOpen by remember { mutableStateOf(false) }
  fun select(color: ULong) {
    dialogOpen = false
    onChangeColor(color)
  }
  Row(modifier = Modifier.clickable {
    dialogOpen = !dialogOpen
  }) {
    Canvas(modifier = Modifier.size(30.dp)) {
      drawRect(color = Color(currentColor))
    }
    Text("color")
  }
  if (dialogOpen) {
    Dialog(
      state = DialogState(width = 400.dp, height = 400.dp, position = WindowPosition(Alignment.TopStart)),
      onCloseRequest = {
        dialogOpen = false
      }
    ) {
      Column {
        TxtButton("Blue") {
          select(Color.Blue.value)
        }
        TxtButton("Red") {
          select(Color.Red.value)
        }
        TxtButton("Yellow") {
          select(Color.Yellow.value)
        }
      }
    }
  }
}
