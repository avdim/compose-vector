package com.usage

import androidx.compose.runtime.*
import lib.vector.DisplayMode
import lib.vector.Pt
import kotlin.random.Random

@Composable
fun SnowDrifts() {
  val SNOW_DRIFT_DY = -25f
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

              val baseGray = 195
              val diffGray = (0..25).random()
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