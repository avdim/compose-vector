package lib.vector

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.jvm.JvmName

@Composable
expect fun GeneratedLayer(modifier: Modifier, lambda: GeneratedScope.() -> Unit)
