@file:OptIn(ExperimentalStdlibApi::class)

package com.usage

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
    SnowDrifts()
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
        Pt(leftX + stepWidth * it, 200f + Random.nextInt(0, 80))
      }
    )
  }
  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { it }
      curvePoints = curvePoints
        .map { Pt(it.x + speed, it.y) }
        .toMutableList().apply {
          indices.reversed().forEach { i->
            if(this[i].x > rightX) {
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


@Composable
fun SnowDrifts() {
  val leftX = -3000f
  val rightX = 2000f
  val bottomY = 800f
  val bottomLeft = Pt(leftX, bottomY)
  val bottomRight = Pt(rightX, bottomY)
  val pointsCount = 20
  val stepWidth = (rightX - leftX) / pointsCount
  val speed = 3f

  data class SnowDriftData(
    val dx:Float,
    val pathPoints:List<Pt>,
    val color:ULong
  )

  var snowDrifts: List<SnowDriftData> by remember {
    mutableStateOf(
      List(pointsCount) {
        val x = leftX + it * stepWidth
        val pathPoints = listOf(
          listOf(Pt(11, 753),Pt(135, 630),Pt(358, 578),Pt(535, 623),Pt(663, 747),),
          listOf(Pt(37, 749),Pt(110, 663),Pt(225, 617),Pt(378, 660),Pt(445, 750),),
          listOf(Pt(97, 756),Pt(204, 623),Pt(359, 572),Pt(523, 630),Pt(647, 747),)
        ).random()

        val baseGray = 180
        val diffGray = (0..50).random()
        val c = (baseGray + diffGray).toULong()
        val color: ULong = 0xff00000000000000uL + (c shl 32) + (c shl 40) + (c shl 48)
        SnowDriftData(x, pathPoints, color)
      }
    )
  }
  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { it }
      snowDrifts = snowDrifts
        .map { it.copy(dx = it.dx + speed) }
        .toMutableList().apply {
          indices.reversed().forEach { i->
            if(this[i].dx > rightX) {
              val moveMe = removeAt(i)
              add(0, moveMe.copy(dx = leftX))
            }
          }
        }
    }
  }
  DisplayMode() {
    snowDrifts.forEach { data->
      drawCurve(data.color, data.pathPoints.map { Pt(it.x + data.dx, it.y + SNOW_DRIFT_DY) }, fillPath = true)
    }
  }
}
