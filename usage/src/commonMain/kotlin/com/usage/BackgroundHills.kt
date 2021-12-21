package com.usage

import androidx.compose.runtime.*
import lib.vector.DisplayMode
import lib.vector.Pt
import kotlin.random.Random

@Composable
fun BackgroundHills() {
  val color = remember {
      val c = (180 + 60).toULong()
      0xff00000000000000uL + (c shl 32) + (c shl 40) + (c shl 48)
  }
  val middleY = 300f
  val leftX = -3000f
  val rightX = 2000f
  val bottomY = 800f
  val bottomLeft = Pt(leftX, bottomY)
  val bottomRight = Pt(rightX, bottomY)
  val pointsCount = 15
  val stepWidth = (rightX - leftX) / pointsCount
  val speed = 1.3f
  var curvePoints: List<Pt> by remember {
      mutableStateOf(
          List(pointsCount) {
              Pt(leftX + stepWidth * it, middleY + Random.nextInt(0, 80))
          }
      )
  }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { it }
            curvePoints = curvePoints
                .map { Pt(it.x + speed, it.y) }
                .toMutableList().apply {
                    indices.reversed().forEach { i ->
                        if (this[i].x > rightX) {
                            val moveMe = removeAt(i)
                            add(0, Pt(leftX, moveMe.y))
                        }
                    }
                }
        }
    }
    DisplayMode() {
        drawCurve(color, listOf(bottomLeft) + curvePoints + listOf(bottomRight), emptyMap(), fillPath = true)
    }
}