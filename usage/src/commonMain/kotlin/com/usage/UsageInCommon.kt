package com.usage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(154, 313),Pt(159, 324),Pt(205, 338),Pt(223, 407),Pt(212, 466),Pt(169, 520),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(354, 480),Pt(504, 485),Pt(548, 535),Pt(516, 563),Pt(496, 562),Pt(501, 585),Pt(534, 585),Pt(591, 529),Pt(579, 500),Pt(577, 471),Pt(624, 460),Pt(626, 340),Pt(605, 265),Pt(576, 210),Pt(595, 173),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(241, 221),Pt(225, 237),Pt(160, 575),Pt(108, 546),Pt(232, 519),Pt(167, 285),)

  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(154, 313),Pt(159, 324),Pt(205, 338),Pt(242, 429),Pt(316, 477),Pt(337, 550),Pt(312, 593),Pt(365, 570),Pt(373, 526),Pt(405, 485),Pt(504, 485),Pt(548, 535),Pt(516, 563),Pt(496, 562),Pt(501, 585),Pt(534, 585),Pt(591, 529),Pt(579, 500),Pt(577, 471),Pt(624, 460),Pt(626, 340),Pt(605, 265),Pt(576, 210),Pt(595, 173),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(241, 221),Pt(225, 237),Pt(352, 603),Pt(283, 566),Pt(384, 550),Pt(167, 285),)


  var animate: Boolean by remember { mutableStateOf(false) }
  val animatedInt by animateIntAsState(if (animate) 100 else 0)
  val animatedFloat by animateFloatAsState(if (animate) 1f else 0f)

  val animatedPoints: List<Pt> by derivedStateOf {
    val f = animatedFloat
    points1.mapIndexed { i, pt -> pt + (points2[i] - pt) * f }
  }
  CatBitmap()
  GeneratedLayer(Modifier) {
    val l = animatedPoints
//    val l = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(154, 313),Pt(159, 324),Pt(205, 338),Pt(242, 429),Pt(316, 477),Pt(337, 550),Pt(312, 593),Pt(365, 570),Pt(373, 526),Pt(405, 485),Pt(504, 485),Pt(548, 535),Pt(516, 563),Pt(496, 562),Pt(501, 585),Pt(534, 585),Pt(591, 529),Pt(579, 500),Pt(577, 471),Pt(624, 460),Pt(626, 340),Pt(605, 265),Pt(576, 210),Pt(595, 173),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(241, 221),Pt(225, 237),Pt(352, 603),Pt(283, 566),Pt(384, 550),Pt(167, 285),)


    drawCurve(0x550000ff00000000uL,listOf(l[0],l[1],l[2],l[3],l[4],l[5],l[6],l[7],l[8],l[9],l[10],l[11],l[12],l[13],l[14],l[15],l[16],l[17],l[18],l[19],l[20],l[21],l[22],l[23],l[24],l[25],l[26],l[27],l[28],l[29],l[30],l[31],l[32],l[33],l[34],l[35],l[36],l[37],l[0],), mapOf(l[10] to BezierRef(l[38], l[39]),l[11] to BezierRef(l[40], null),l[2] to BezierRef(l[41], null),),)

  }

  Column {
    TxtButton("animate") {
      animate = !animate
    }
    Text("${animatedInt}")
  }

}
