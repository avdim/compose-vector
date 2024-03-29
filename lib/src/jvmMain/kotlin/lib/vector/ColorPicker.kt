package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition

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
      state = DialogState(width = 500.dp, height = 500.dp, position = WindowPosition(Alignment.TopStart)),
      onCloseRequest = {
        dialogOpen = false
      }
    ) {
      ColorPallet(currentColor) {
        select(it)
      }
    }
  }
}

@Composable
fun ColorPallet(initColor: ULong, onSelect: (ULong) -> Unit) {
  Column {
    Row {
      listOf(Color.Red, Color.Green, Color.Blue, Color.Black, Color.Gray, Color.Yellow, Color.Cyan).forEach {
        val width = 40f
        val height = 40f
        Canvas(Modifier.size(width.dp, height.dp).clickable {
          onSelect(it.value)
        }) {
          drawRect(color = it, size = Size(width, height))
        }
      }
    }
    Divider(Modifier.size(5.dp))
    var red: Int by remember { mutableStateOf((Color(initColor).red * 0xFF).toInt()) }
    var green: Int by remember { mutableStateOf((Color(initColor).green * 0xFF).toInt()) }
    var blue: Int by remember { mutableStateOf((Color(initColor).blue * 0xFF).toInt()) }
    val currentColor: ULong by derivedStateOf { Color(red, green, blue).value }

    Canvas(Modifier.size(50.dp, 50.dp).clickable {
      onSelect(currentColor)
    }) {
      drawRect(Color(currentColor), size = Size(50f, 50f))
    }
    Divider(Modifier.size(5.dp))
    Row {
      Canvas(Modifier.size(256.dp, 256.dp).pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            if (event.buttons.isPrimaryPressed) {
              red = event.changes.first().position.x.toInt()
              green = event.changes.first().position.y.toInt()
            }
          }
        }
      }) {
        for (r in 0..0xFF) {
          for (g in 0..0xFF) {
            drawRect(
              color = Color(red = r, green = g, blue = blue),
              topLeft = Offset(r.toFloat(), g.toFloat()),
              size = Size(1f, 1f)
            )
          }
        }
      }
      val BAND_WIDTH = 40
      Canvas(Modifier.size(BAND_WIDTH.dp, 256.dp).pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val event = awaitPointerEvent()
            if (event.buttons.isPrimaryPressed) {
              blue = event.changes.first().position.y.toInt()
            }
          }
        }
      }) {
        for (b in 0..0xFF) {
          drawRect(color = Color(0, 0, b), topLeft = Offset(0f, b.toFloat()), size = Size(BAND_WIDTH.toFloat(), 1f))
        }
      }
    }
    TxtButton("Done") {
      onSelect(currentColor)
    }
  }
}
