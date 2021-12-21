import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import com.usage.runSimpleComposableWindow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import lib.vector.TxtButton

@OptIn(InternalCoroutinesApi::class)
@Composable
fun testSnapshotFlow() {
  val someState: MutableState<Int> = remember { mutableStateOf(1) }
  LaunchedEffect(Unit) {
    snapshotFlow { someState.value }
      .filter { it % 2 == 0 }
      .collect(object : FlowCollector<Int> {
        override suspend fun emit(value: Int) {
          println("collect $value")
        }
      })
  }

}

fun main() {
  runSimpleComposableWindow {
    var clicksCount by remember { mutableStateOf(0) }
    Column {
      TxtButton("Increment $clicksCount") {
        clicksCount++
      }
    }
    LaunchedEffect(Unit) {
      snapshotFlow { clicksCount }
        .filter { it % 2 == 0 }
        .collect {
          println("flow $it")
        }
    }
  }
}
