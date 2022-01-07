import androidx.compose.ui.graphics.Color
import com.usage.runSimpleComposableWindow
import lib.vector.OpenColorPicker

fun main() {
  runSimpleComposableWindow() {
    OpenColorPicker(initColor = Color.Red.value) {  }
  }
}
