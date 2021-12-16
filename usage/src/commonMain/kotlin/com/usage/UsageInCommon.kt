package com.usage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(223, 407),Pt(212, 466),Pt(169, 520),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(364, 470),Pt(463, 467),Pt(531, 500),Pt(531, 549),Pt(499, 567),Pt(498, 589),Pt(534, 585),Pt(577, 542),Pt(592, 492),Pt(602, 466),Pt(625, 419),Pt(626, 340),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(160, 575),Pt(108, 546),Pt(232, 519),Pt(167, 285),Pt(247, 233),Pt(232, 228),Pt(607, 190),Pt(546, 529),)

  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(245, 431),Pt(310, 474),Pt(335, 543),Pt(309, 597),Pt(368, 564),Pt(377, 509),Pt(400, 482),Pt(475, 472),Pt(579, 473),Pt(667, 503),Pt(727, 520),Pt(746, 574),Pt(777, 565),Pt(760, 515),Pt(737, 475),Pt(693, 438),Pt(662, 386),Pt(631, 342),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(365, 601),Pt(289, 568),Pt(385, 536),Pt(167, 285),Pt(232, 228),Pt(247, 233),Pt(607, 190),Pt(612, 477),)


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
    drawCurve(0x5500ff0000000000uL,listOf(l[0],l[1],l[2],l[3],l[4],l[5],l[6],l[7],l[8],l[9],l[10],l[11],l[12],l[13],l[14],l[15],l[16],l[17],l[18],l[19],l[20],l[21],l[22],l[23],l[24],l[25],l[26],l[27],l[28],l[29],l[30],l[31],l[32],l[33],l[34],l[35],l[36],l[37],l[0],), mapOf(l[10] to BezierRef(l[38], l[39]),l[11] to BezierRef(l[40], null),l[2] to BezierRef(l[41], null),l[4] to BezierRef(null, null),l[36] to BezierRef(l[43], l[42]),l[27] to BezierRef(null, l[44]),l[15] to BezierRef(l[45], null),l[16] to BezierRef(null, null),),)


  }

  Box(Modifier.fillMaxSize()) {
    Column(Modifier.align(Alignment.BottomStart)) {
      TxtButton("animate") {
        animate = !animate
      }
      Text("${animatedInt}")
    }
  }

}
