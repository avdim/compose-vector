import androidx.compose.ui.graphics.Color
import com.usage.runSimpleComposableWindow
import lib.vector.ColorPallet

fun main() {
  runSimpleComposableWindow() {
    ColorPallet(initColor = Color.Red.value) {  }
  }
}
