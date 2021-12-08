import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

//Composables with a return type should be named the way you'd name a normal Kotlin function,
// starting with a lowercase letter.
@Composable
fun testProduceState(): State<String> {
  val key = Unit
  return produceState("Init", key) {
    value = "Second"
    delay(1)
    value = "Third"
    awaitDispose {
      println("on dispose")
    } // return's Nothing
    println("Unreachable code")
  }
}
