import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.usage.runSimpleComposableWindow

@Composable
fun todoAnimations() {
  val backgroundColor by animateColorAsState(if (true) Color.Gray else Color.Yellow)

}

@Composable
fun todoInfiniteAnimation() {
  val infiniteTransition = rememberInfiniteTransition()
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1000
        0.7f at 500
      },
      repeatMode = RepeatMode.Reverse
    )
  )
  Text("Hello", modifier = Modifier.alpha(alpha))
}

fun main() {
  runSimpleComposableWindow() {

  }
}
