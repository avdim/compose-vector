@file:OptIn(ExperimentalStdlibApi::class)

package com.usage

import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import lib.vector.Pt
import kotlin.random.Random

const val DRAW_SNOW_DRIFT = true
const val SNOW_DRIFT_DY = -25f
const val DRAW_CHRISTMAS_TREE = true

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun UsageInCommon(modifier: Modifier = Modifier) {
  if (DRAW_SNOW_DRIFT) {
    BackgroundHills()
  }

  if (DRAW_CHRISTMAS_TREE) {

  }

  Cat(modifier)

  if (DRAW_SNOW_DRIFT) {
    SnowDrifts()
  }

  GeneratedLayer(modifier) {

  }
}

@Composable
fun ChristmasTree(x: Float, y:Float) {
  val speed = 1.3f
  DisplayMode {
    val left = 0
    val right = 80
    val centerX = (right + left) / 2
    val top = 240
    val bottom = 300
    val stepHeight = 40
    val stepNarrowWidth = 8
    val color = 0xff00990000000000uL
    val repeatCount = 3
    val trunkWidth = 30
    val trunkHeight = 40
    val trunkY = bottom + repeatCount - 20
    drawRect(0xff65432100000000uL, Pt(x + centerX - trunkWidth / 2, y + trunkY), Pt(x + centerX + trunkWidth / 2, y + trunkY + trunkHeight))
    repeat(repeatCount) {
      val topPt = Pt(x + centerX, y + top - it * stepHeight)
      val bottomLeft = Pt(x + left + it * stepNarrowWidth, y + bottom - it * stepHeight)
      val bottomRight = Pt(x + right - it * stepNarrowWidth, y + bottom - it * stepHeight)
      drawCurve(color, listOf(topPt, bottomLeft, bottomRight, topPt), mapOf(), fillPath = true)
    }
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


@Composable
fun SnowDrifts() {
  val leftX = -3000f
  val rightX = 2000f
  val pointsCount = 20
  val stepWidth = (rightX - leftX) / pointsCount
  val speed = 3f

  data class SnowDriftData(
    val dx: Float,
    val pathPoints: List<Pt>,
    val color: ULong
  )

  var snowDrifts: List<SnowDriftData> by remember {
    mutableStateOf(
      List(pointsCount) {
        val x = leftX + it * stepWidth
        val pathPoints = listOf(
          listOf(Pt(11, 755), Pt(135, 630), Pt(358, 578), Pt(535, 623), Pt(663, 755)),
          listOf(Pt(37, 755), Pt(110, 663), Pt(225, 617), Pt(378, 660), Pt(445, 755)),
          listOf(Pt(97, 755), Pt(204, 623), Pt(359, 572), Pt(523, 630), Pt(647, 755)),
          listOf(Pt(10, 755), Pt(192, 606), Pt(352, 598), Pt(458, 663), Pt(507, 717), Pt(557, 755)),
          listOf(Pt(65, 755), Pt(166, 641), Pt(258, 629), Pt(342, 575), Pt(431, 635), Pt(495, 755)),
        ).random()

        val baseGray = 180
        val diffGray = (0..50).random()
        val c = (baseGray + diffGray).toULong()
        val color: ULong = 0xff00000000000000uL + (c shl 32) + (c shl 40) + (c shl 48)
        SnowDriftData(x, pathPoints, color)
      }.shuffled()
    )
  }
  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos { it }
      snowDrifts = snowDrifts
        .map { it.copy(dx = it.dx + speed) }
        .toMutableList().apply {
          indices.reversed().forEach { i ->
            if (this[i].dx > rightX) {
              val moveMe = removeAt(i)
              add(Random.nextInt(0, 4), moveMe.copy(dx = leftX))
            }
          }
        }
    }
  }
  DisplayMode() {
    snowDrifts.forEach { data ->
      drawCurve(data.color, data.pathPoints.map { Pt(it.x + data.dx, it.y + SNOW_DRIFT_DY) }, fillPath = true)
    }
  }
}
