import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.usage.runSimpleComposableWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() = runSimpleComposableWindow {
  Box(
    Modifier.fillMaxSize().onPointerEvent(PointerEventType.Scroll) {
      val scrollDelta = it.changes.first().scrollDelta
      println("scrollDelta: $scrollDelta")
    }
  )
}
