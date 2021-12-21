package com.usage

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HappyNewYearText() {
  val infiniteTransition = rememberInfiniteTransition()
  val animationRatio by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1000
        0.2f at 300
        0.8f at 700
      },
      repeatMode = RepeatMode.Reverse
    )
  )

  Box(Modifier.fillMaxSize().padding(top = 30.dp)) {
    Column(
      modifier = Modifier.align(Alignment.TopCenter)
        .background(color = Color(0x99000000))
        .graphicsLayer {
          this.shadowElevation = 5f
        }
    ) {
      Text(
        text = "@Composable",
        style = TextStyle(
          fontStyle = FontStyle.Italic,
          color = Color(0xFFffFFff),
          fontSize = 36.sp,
          textGeometricTransform = TextGeometricTransform(scaleX = 2f, skewX = animationRatio * 10)
        )
      )
      Text(
        text = "fun HappyNewYear() {",
        style = TextStyle(
          fontStyle = FontStyle.Normal,
          color = Color(0xFFffFFff),
          fontSize = 36.sp,
          textGeometricTransform = TextGeometricTransform(scaleX = 2f, skewX = animationRatio * 10)
        )
      )

    }
  }
}
