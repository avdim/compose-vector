import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.*
import com.usage.runSimpleComposableWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() = runSimpleComposableWindow {
  Box(
    Modifier.fillMaxSize()
      .pointerInput(Unit) {
        while (true) {
          val event = awaitPointerEventScope {
            awaitPointerEvent()
          }
          if (event.type == PointerEventType.Scroll) {
            println("scroll $event")
            event.changes.first().scrollDelta.y
            event.keyboardModifiers.isShiftPressed//right
            event.keyboardModifiers.isCtrlPressed//zoom
          }
        }
      }
  )
}
