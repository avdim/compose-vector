package com.usage

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(223, 407),Pt(214, 471),Pt(162, 518),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(365, 478),Pt(446, 473),Pt(583, 473),Pt(667, 502),Pt(713, 536),Pt(732, 572),Pt(765, 577),Pt(765, 533),Pt(733, 490),Pt(701, 445),Pt(658, 379),Pt(626, 340),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(160, 575),Pt(111, 532),Pt(232, 519),Pt(167, 285),Pt(247, 233),Pt(232, 228),Pt(607, 190),Pt(641, 486),)
  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(245, 431),Pt(310, 474),Pt(331, 538),Pt(309, 597),Pt(368, 564),Pt(377, 509),Pt(392, 472),Pt(454, 468),Pt(501, 475),Pt(535, 510),Pt(519, 548),Pt(487, 575),Pt(520, 592),Pt(552, 571),Pt(581, 536),Pt(598, 484),Pt(624, 425),Pt(631, 342),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(365, 601),Pt(289, 568),Pt(385, 536),Pt(167, 285),Pt(247, 233),Pt(232, 228),Pt(607, 190),Pt(528, 488),)

//  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(223, 407),Pt(212, 466),Pt(169, 520),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(364, 470),Pt(463, 467),Pt(531, 500),Pt(531, 549),Pt(499, 567),Pt(498, 589),Pt(534, 585),Pt(577, 542),Pt(592, 492),Pt(602, 466),Pt(625, 419),Pt(626, 340),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(160, 575),Pt(108, 546),Pt(232, 519),Pt(167, 285),Pt(247, 233),Pt(232, 228),Pt(607, 190),Pt(546, 529),)
//  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(245, 431),Pt(310, 474),Pt(335, 543),Pt(309, 597),Pt(368, 564),Pt(377, 509),Pt(400, 482),Pt(475, 472),Pt(579, 473),Pt(667, 503),Pt(727, 520),Pt(746, 574),Pt(777, 565),Pt(760, 515),Pt(737, 475),Pt(693, 438),Pt(662, 386),Pt(631, 342),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(365, 601),Pt(289, 568),Pt(385, 536),Pt(167, 285),Pt(247, 233),Pt(232, 228),Pt(607, 190),Pt(612, 477),)


  val infiniteTransition = rememberInfiniteTransition()
  val animatedFloat by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1000
        0.2f at 300
        0.8f at 700
      },
      repeatMode = RepeatMode.Reverse
    )
  )

  val animatedPointsA: List<Pt> by derivedStateOf {
    val f = animatedFloat
    points1.mapIndexed { i, pt -> pt + (points2[i] - pt) * f }
  }
  val animatedPointsB: List<Pt> by derivedStateOf {
    val f = animatedFloat
    points2.mapIndexed { i, pt -> pt + (points1[i] - pt) * f }
  }
//  CatBitmap()
  GeneratedLayer(Modifier) {
    val a = animatedPointsA
    drawCurve(0xff00009900000000uL,listOf(a[0],a[1],a[2],a[3],a[4],a[5],a[6],a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19],a[20],a[21],a[22],a[23],a[24],a[25],a[26],a[27],a[28],a[29],a[30],a[31],a[32],a[33],a[34],a[35],a[36],a[37],a[0],), mapOf(a[10] to BezierRef(a[38], a[39]),a[11] to BezierRef(a[40], null),a[2] to BezierRef(a[41], null),a[36] to BezierRef(a[43], a[42]),a[27] to BezierRef(null, a[44]),a[15] to BezierRef(a[45], null),),)

    val b = animatedPointsB
    drawCurve(0xFF0000cc00000000uL,listOf(b[0],b[1],b[2],b[3],b[4],b[5],b[6],b[7],b[8],b[9],b[10],b[11],b[12],b[13],b[14],b[15],b[16],b[17],b[18],b[19],b[20],b[21],b[22],b[23],b[24],b[25],b[26],b[27],b[28],b[29],b[30],b[31],b[32],b[33],b[34],b[35],b[36],b[37],b[0],), mapOf(b[10] to BezierRef(b[38], b[39]),b[11] to BezierRef(b[40], null),b[2] to BezierRef(b[41], null),b[36] to BezierRef(b[43], b[42]),b[27] to BezierRef(null, b[44]),b[15] to BezierRef(b[45], null),),)

  }

}

