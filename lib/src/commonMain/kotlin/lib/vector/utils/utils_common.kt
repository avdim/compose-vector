package lib.vector.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path

inline fun Path.moveTo(x: Int, y: Int) {
  moveTo(x.toFloat(), y.toFloat())
}

inline fun Path.lineTo(x: Int, y: Int) {
  lineTo(x.toFloat(), y.toFloat())
}

expect fun ImageBitmap.toByteArray(): ByteArray
expect fun ByteArray.toImageBitmap(): ImageBitmap

fun <T> List<T>.indexOfFirstOrNull(lambda:(T)->Boolean):Int? {
  val result = indexOfFirst(lambda)
  if (result == -1) {
    return null
  }
  return result
}
