import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun LandingScreen(modifier: Modifier = Modifier, onTimeout: () -> Unit) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    // This will always refer to the latest onTimeout function that
    // LandingScreen was recomposed with
    val currentOnTimeout by rememberUpdatedState(onTimeout) // IMPORTANT HERE

    // Create an effect that matches the lifecycle of LandingScreen.
    // If LandingScreen recomposes or onTimeout changes,
    // the delay shouldn't start again.
    LaunchedEffect(true) {
      delay(500)
      currentOnTimeout()
    }

//    Image(painterResource(id = R.drawable.ic_crane_drawer), contentDescription = null)
  }
}
