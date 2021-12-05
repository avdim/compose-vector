import androidx.compose.runtime.Composable
import androidx.compose.runtime.*

@Composable
fun TestDisposableEffect() {
  DisposableEffect(key1 = Unit, key2 = Unit) {
    // Make MapView follow the current lifecycle
//    val lifecycleObserver = getMapLifecycleObserver(mapView)
//    lifecycle.addObserver(lifecycleObserver)
    onDispose {
//      lifecycle.removeObserver(lifecycleObserver)
    }
  }
}
