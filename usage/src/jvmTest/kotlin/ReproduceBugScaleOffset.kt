import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication

//https://github.com/JetBrains/compose-jb/issues/1559
fun main() = singleWindowApplication {
  Box(modifier = Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .offset(-100.dp, 0.dp)
        .scale(0.95f)
        .offset(200.dp, 20.dp)
        .size(100.dp)
        .background(Color.Green)
    )
    Box(
      modifier = Modifier
        .offset(0.dp, 0.dp)
        .scale(0.95f)
        .offset(200.dp, 20.dp)
        .size(100.dp)
        .background(Color.Red)
    )
  }
}
