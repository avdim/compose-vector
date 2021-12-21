
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import com.usage.runSimpleClickerWindow


fun main() = runSimpleClickerWindow { clicksCount ->
  Text("clicksCount: $clicksCount")
  SideEffect {
    //On every successful composition
    println("side effect, clicksCount: $clicksCount")
  }
}
