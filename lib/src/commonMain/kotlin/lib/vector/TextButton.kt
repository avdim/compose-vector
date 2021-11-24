package lib.vector

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun TextButton(text: String, onClick: () -> Unit) {
  Button(onClick = onClick) {
    Text(text)
  }
}
