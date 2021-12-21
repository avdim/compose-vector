@file:OptIn(ExperimentalStdlibApi::class)

package com.usage

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size

@Composable
fun HappyNewYear() {
  StarsAndSky(Size(800f, 400f))
  BackgroundHills()
  ManyChristmasTrees()
  Snow(0.7f, 1.0f, 40)
  Cat()
  Snow(1.0f, 1.0f, 40)
  SnowDrifts()
  Snow(1.5f, 2f, 50)
  HappyNewYearText()
}
