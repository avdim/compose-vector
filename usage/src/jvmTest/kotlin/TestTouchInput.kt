import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.usage.runSimpleComposableWindow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun main() {
  runSimpleComposableWindow {
    MoveBoxWhereTapped()
  }
}

@Composable
fun MoveBoxWhereTapped() {
  // Creates an `Animatable` to animate Offset and `remember` it.
  val animatedOffset = remember {
    Animatable(Offset(0f, 0f), androidx.compose.ui.geometry.Offset.VectorConverter)
  }

  Box(
    // The pointerInput modifier takes a suspend block of code
    Modifier.fillMaxSize().pointerInput(Unit) {
      // Create a new CoroutineScope to be able to create new
      // coroutines inside a suspend function
      coroutineScope {
        while (true) {
          // Wait for the user to tap on the screen
          val offset = awaitPointerEventScope {
            awaitFirstDown().position
          }
          // Launch a new coroutine to asynchronously animate to where
          // the user tapped on the screen
          launch {
            // Animate to the pressed position
            animatedOffset.animateTo(offset)
          }
        }
      }
    }
  ) {
    Text("Tap anywhere", Modifier.align(Alignment.Center))
    Box(
      Modifier
        .offset {
          // Use the animated offset as the offset of this Box
          IntOffset(
            animatedOffset.value.x.roundToInt(),
            animatedOffset.value.y.roundToInt()
          )
        }
        .size(40.dp)
        .background(Color(0xff3c1361), CircleShape)
    )
  }
}

