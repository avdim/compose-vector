package com.usage

import androidx.compose.animation.core.*
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(223, 407),Pt(214, 471),Pt(162, 518),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(365, 478),Pt(446, 473),Pt(583, 473),Pt(667, 502),Pt(713, 536),Pt(732, 572),Pt(765, 577),Pt(765, 533),Pt(733, 490),Pt(701, 445),Pt(658, 379),Pt(626, 340),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(160, 575),Pt(111, 532),Pt(232, 519),Pt(167, 285),Pt(232, 228),Pt(247, 233),Pt(607, 190),Pt(641, 486),)
  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(245, 431),Pt(310, 474),Pt(331, 538),Pt(309, 597),Pt(368, 564),Pt(377, 509),Pt(392, 472),Pt(454, 468),Pt(501, 475),Pt(535, 510),Pt(519, 548),Pt(487, 575),Pt(520, 592),Pt(552, 571),Pt(581, 536),Pt(598, 484),Pt(624, 425),Pt(631, 342),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(365, 601),Pt(289, 568),Pt(385, 536),Pt(167, 285),Pt(232, 228),Pt(247, 233),Pt(607, 190),Pt(528, 488),)

  val m00 = Pt(205, 211)
  val m01 = Pt(201, 242)
  val m02 = Pt(169, 259)
  val m03 = Pt(147, 292)
  val m04 = Pt(155, 311)
  val m05 = Pt(173, 324)
  val m06 = Pt(205, 338)

  val m32 = Pt(489, 329)
  val m33 = Pt(385, 336)
  val m34 = Pt(304, 305)
  val m35 = Pt(257, 256)
  val m43 = Pt(247, 233)
  val m36 = Pt(239, 214)
  val m42 = Pt(232, 228)
  val m37 = Pt(225, 237)

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
    drawCurve(0xff00009900000000uL,listOf(m00,m01,m02,m03,m04,m05,m06,a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19],a[20],a[21],a[22],a[23],a[24],a[25],a[26],a[27],a[28],a[29],a[30],a[31],m32,m33,m34,m35,m36,m37,m00,), mapOf(a[10] to BR(a[38], a[39]),a[11] to BR(a[40], null),a[2] to BR(a[41], null),m36 to BR(m42, m43),a[27] to BR(null, a[44]),a[15] to BR(a[45], null),),)
    val b = animatedPointsB
    drawCurve(0xFF0000cc00000000uL,listOf(m00,m01,m02,m03,m04,m05,m06,b[7],b[8],b[9],b[10],b[11],b[12],b[13],b[14],b[15],b[16],b[17],b[18],b[19],b[20],b[21],b[22],b[23],b[24],b[25],b[26],b[27],b[28],b[29],b[30],b[31],m32,m33,m34,m35,m36,m37,m00,), mapOf(b[10] to BR(b[38], b[39]),b[11] to BR(b[40], null),b[2] to BR(b[41], null),m36 to BR(m42, m43),b[27] to BR(null, b[44]),b[15] to BR(b[45], null),),)
  }

}

