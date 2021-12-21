package com.usage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun Snow(speed:Float, snowFlakeSize:Float = 1.0f, count:Int) {
  val leftX = -10f
  val topY = -10f
  val speedX = 1.1f*speed
  val speedY = 2.2f*speed
  val width = 800 + 20
  val height = 800 + 20
  val rightX = leftX + width
  val bottomY = topY + height

  data class SnowFlake(val size: Float, val x: Float, val y: Float)

  var snowFlakes:List<SnowFlake> by remember {
      mutableStateOf(
          List(count) {
              SnowFlake(
                  snowFlakeSize * (1f + 1f * Random.nextFloat()),
                  Random.nextFloat() * width,
                  Random.nextFloat() * height
              )
          }
      )
  }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { it }
            snowFlakes = snowFlakes
                .map { it.copy(x = it.x + speedX, y = it.y + speedY) }
                .toMutableList().apply {
                    indices.reversed().forEach { i ->
                        if (this[i].x > rightX || this[i].y > bottomY) {
                            val moveMe = removeAt(i)
                            if (Random.nextBoolean()) {
                                add(moveMe.copy(x = leftX, y = topY + height * Random.nextFloat()))
                            } else {
                                add(moveMe.copy(x = leftX + width * Random.nextFloat(), y = topY))
                            }

                        }
                    }
                }
        }
    }

    Canvas(Modifier.wrapContentSize(Alignment.Center).fillMaxSize()) {
        snowFlakes.forEach {
            drawCircle(Color.White, it.size, Offset(it.x, it.y))
        }
    }
}