import androidx.compose.runtime.*

@Composable
fun TestMutableStateListOf() {
  var someIndex by remember { mutableStateOf(0) }
  val listState = mutableStateListOf<String>("a", "b", "c")
  listState[someIndex] = "aa"
}
