@file:OptIn(ExperimentalStdlibApi::class)

package com.usage

import androidx.compose.animation.core.*
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import lib.vector.Pt
import kotlin.random.Random

const val DRAW_SNOW_DRIFT = true
const val SNOW_DRIFT_DY = -30f

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun UsageInCommon(modifier: Modifier = Modifier) {
  if(DRAW_SNOW_DRIFT) {
    BackgroundHills()
  }

  Cat(modifier)

  if (DRAW_SNOW_DRIFT) {
    var snowDiffX by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
      while (true) {
        withFrameNanos { it }
        snowDiffX += 3f
      }
    }
    repeat(20) {
      SnowDrift(modifier, dx = snowDiffX + remember { Random.nextInt(-3000, 2000).toFloat() })
    }
  }

  GeneratedLayer(modifier) {

  }
}

@Composable
fun BackgroundHills() {
  val color = remember {
    val c = (180 + 60).toULong()
    0xff00000000000000uL + (c shl 32) + (c shl 40) + (c shl 48)
  }
  var backgroundDiffX by remember { mutableStateOf(0f) }
  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { it }
      backgroundDiffX += 1.3f
    }
  }
  val curvePoints = remember {
    val leftX = -3000
    val rightX = 2000
    val bottomLeft = Pt(-3000, 800)
    val bottomRight = Pt(2000, 800)
    val pointsCount = 15
    val stepWidth = (rightX - leftX) / 15
    val points = List(pointsCount) {
      Pt(leftX + stepWidth * it, 200 + Random.nextInt(0, 80))
    }
    buildList {
      add(bottomLeft)
      addAll(points)
      add(bottomRight)
    }
  }
  DisplayMode() {
    drawCurve(color, curvePoints.map { it.copy(x = it.x + backgroundDiffX) }, emptyMap(), fillPath = true)
  }
}

@Composable
fun SnowDrift(modifier:Modifier, dx:Float, dy:Float = 0f) {
  val pathPoints = remember {
    listOf(
      listOf(Pt(11, 753),Pt(135, 630),Pt(358, 578),Pt(535, 623),Pt(663, 747),),
      listOf(Pt(37, 749),Pt(110, 663),Pt(225, 617),Pt(378, 660),Pt(445, 750),),
      listOf(Pt(97, 756),Pt(204, 623),Pt(359, 572),Pt(523, 630),Pt(647, 747),)
    ).random()
  }
  val color: ULong = remember {
    val baseGray = 180
    val diffGray = (0..50).random()
    val c = (baseGray + diffGray).toULong()

    0xff00000000000000uL + (c shl 32) + (c shl 40) + (c shl 48)
  }
  val diffPoints = pathPoints.map { it.copy(x = it.x + dx, y = it.y + dy + SNOW_DRIFT_DY) }
  DisplayMode(modifier) {
    drawCurve(color, diffPoints, fillPath = true)
  }
}
