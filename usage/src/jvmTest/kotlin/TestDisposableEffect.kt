import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import com.usage.runSimpleComposableWindow

@Composable
fun TestDisposableEffect(someArg:Int) {
  Text("TestDisposableEffect $someArg")
  DisposableEffect(key1 = someArg, key2 = Unit) {
//  lifecycle.addObserver(lifecycleObserver)
    println("add subsciption")
    onDispose {
      println("remove subsciption")
//    lifecycle.removeObserver(lifecycleObserver)
    }
  }
}

fun main() = runSimpleComposableWindow {
  var counter by remember { mutableStateOf(0) }
  Column {
    TestDisposableEffect(counter)
    Button(onClick = {
        counter++
      }) {
      Text("click $counter")
    }
  }
}
