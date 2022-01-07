package lib.vector

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
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
      OpenColorPicker(currentColor) {
        select(it)
      }
    }
  }
}

@Composable
fun OpenColorPicker(initColor: ULong, onSelect: (ULong) -> Unit) {
  Column {
    var red: Int by remember { mutableStateOf((Color(initColor).red * 0xFF).toInt()) }
    var green: Int by remember { mutableStateOf((Color(initColor).green * 0xFF).toInt()) }
    var blue: Int by remember { mutableStateOf((Color(initColor).blue * 0xFF).toInt()) }
    val currentColor: ULong by derivedStateOf { Color(red, green, blue).value }

    Canvas(Modifier.size(50.dp, 50.dp)) {
      drawRect(Color(currentColor), size = Size(50f, 50f))
    }
    Row {
      Canvas(Modifier.size(256.dp, 256.dp).pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val pointer: PointerInputChange = awaitFirstDown()
            red = pointer.position.x.toInt()
            green = pointer.position.y.toInt()
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
      val BAND_WIDTH = 20
      Canvas(Modifier.size(BAND_WIDTH.dp, 256.dp).pointerInput(Unit) {
        awaitPointerEventScope {
          while (true) {
            val pointer: PointerInputChange = awaitFirstDown()
            blue = pointer.position.y.toInt()
          }
        }
      }) {
        for(b in 0 .. 0xFF) {
          drawRect(color = Color(0,0,b), topLeft = Offset(0f,b.toFloat()), size = Size(BAND_WIDTH.toFloat(), 1f))
        }
      }
    }
    TxtButton("Done") {
      onSelect(currentColor)
    }
  }
}
