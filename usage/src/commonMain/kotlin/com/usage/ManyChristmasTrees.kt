package com.usage

import androidx.compose.runtime.*
import lib.vector.DisplayMode
import lib.vector.Pt
import kotlin.random.Random

@Composable
fun ManyChristmasTrees() {
  val leftX = -3000f
  val rightX = 2000f
  val pointsCount = 40
  val stepWidth = (rightX - leftX) / pointsCount
  val speed = 1.3f

  data class TreeData(
    val x: Float,
    val y: Float,
    val color: ULong
  )

  var trees: List<TreeData> by remember {
      mutableStateOf(
          List(pointsCount) {
              val x = leftX + it * stepWidth + Random.nextFloat() * stepWidth / 3
              val y = Random.nextInt(0, 50).toFloat() + 80
              val baseGreen = 0x55
              val diffGray = (0..25).random()
              val c = (baseGreen + diffGray).toULong()
              val color: ULong = 0xff00000000000000uL + (c shl 40)
              TreeData(x, y, color)
          }.shuffled()
      )
  }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { it }
            trees = trees
                .map { it.copy(x = it.x + speed) }
                .toMutableList().apply {
                    indices.reversed().forEach { i ->
                        if (this[i].x > rightX) {
                            val moveMe = removeAt(i)
                            add(Random.nextInt(0, 4), moveMe.copy(x = leftX))
                        }
                    }
                }
        }
    }
  trees.forEach { data ->
      ChristmasTree(color = data.color, x = data.x, y = data.y)
  }
}

@Composable
fun ChristmasTree(color:ULong = 0xff00990000000000uL, x:Float, y:Float) {
  DisplayMode {
    val left = 0
    val right = 80
    val centerX = (right + left) / 2
    val top = 240
    val bottom = 300
    val stepHeight = 40
    val stepNarrowWidth = 8
    val repeatCount = 3
    val trunkWidth = 30
    val trunkHeight = 40
    val trunkY = bottom + repeatCount - 20
    drawRect(
      0xff65432100000000uL,
      Pt(x + centerX - trunkWidth / 2, y + trunkY),
      Pt(x + centerX + trunkWidth / 2, y + trunkY + trunkHeight)
    )
    repeat(repeatCount) {
      val topPt = Pt(x + centerX, y + top - it * stepHeight)
      val bottomLeft = Pt(x + left + it * stepNarrowWidth, y + bottom - it * stepHeight)
      val bottomRight = Pt(x + right - it * stepNarrowWidth, y + bottom - it * stepHeight)
      drawCurve(color, listOf(topPt, bottomLeft, bottomRight, topPt), mapOf(), fillPath = true)
    }

  }

}