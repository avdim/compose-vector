import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.usage.runSimpleComposableWindow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private fun Modifier.swipeToDismiss(
  onDismissed: () -> Unit
): Modifier = composed {
  // This `Animatable` stores the horizontal offset for the element.
  val offsetX = remember { Animatable(0f) }
  pointerInput(Unit) {
    // Used to calculate a settling position of a fling animation.
    val decay = splineBasedDecay<Float>(this)
    // Wrap in a coroutine scope to use suspend functions for touch events and animation.
    coroutineScope {
      while (true) {
        // Wait for a touch down event.
        val pointerId = awaitPointerEventScope { awaitFirstDown().id }
        // Interrupt any ongoing animation.
        offsetX.stop()
        // Prepare for drag events and record velocity of a fling.
        val velocityTracker = VelocityTracker()
        // Wait for drag events.
        awaitPointerEventScope {
          horizontalDrag(pointerId) { change ->
            // Record the position after offset
            val horizontalDragOffset = offsetX.value + change.positionChange().x
            launch {
              // Overwrite the `Animatable` value while the element is dragged.
              offsetX.snapTo(horizontalDragOffset)
            }
            // Record the velocity of the drag.
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            // Consume the gesture event, not passed to external
            change.consumePositionChange()
          }
        }
        // Dragging finished. Calculate the velocity of the fling.
        val velocity = velocityTracker.calculateVelocity().x
        // Calculate where the element eventually settles after the fling animation.
        val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
        // The animation should end as soon as it reaches these bounds.
        offsetX.updateBounds(
          lowerBound = -size.width.toFloat(),
          upperBound = size.width.toFloat()
        )
        launch {
          if (targetOffsetX.absoluteValue <= size.width) {
            // Not enough velocity; Slide back to the default position.
            offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
          } else {
            // Enough velocity to slide away the element to the edge.
            offsetX.animateDecay(velocity, decay)
            // The element was swiped away.
            onDismissed()
          }
        }
      }
    }
  }
    // Apply the horizontal offset to the element.
    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
}

fun main() {
  runSimpleComposableWindow() {
    Box(
      Modifier.size(200.dp, 50.dp)
        .background(Color.Gray)
        .swipeToDismiss {
          println("swiped")
        }
    ) {
      Text("Swipe me")
    }
  }
}

