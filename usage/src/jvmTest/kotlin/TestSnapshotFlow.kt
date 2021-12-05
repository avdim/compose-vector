import androidx.compose.runtime.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow

@OptIn(InternalCoroutinesApi::class)
@Composable
fun testSnapshotFlow() {
  val someState: MutableState<Int> = remember { mutableStateOf(1) }
  LaunchedEffect(Unit) {
    snapshotFlow { someState.value }
      .filter {it % 2 == 0 }
      .collect(object : FlowCollector<Int> {
        override suspend fun emit(value: Int) {
          println("collect $value")
        }
      })
  }

}
