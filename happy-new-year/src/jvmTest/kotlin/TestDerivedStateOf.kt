import androidx.compose.runtime.*

@Composable
fun TestDerivedStateOf() {
  val myMutableState: MutableState<String> = remember { mutableStateOf("State") }
  val myUpdatedState: State<String> = remember {
    derivedStateOf {
      myMutableState.value + " Derived"
    }
  }
}
