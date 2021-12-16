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

  val head1 = listOf(
    Pt(205, 211),
    Pt(196, 243),
    Pt(169, 259),
    Pt(147, 292),
    Pt(155, 311),
    Pt(173, 324),
    Pt(205, 338),
    Pt(489, 329),
    Pt(385, 336),
    Pt(304, 305),
    Pt(257, 256),
    Pt(239, 214),
    Pt(225, 237),
    Pt(167, 285),
    Pt(229, 227),
    Pt(247, 233),
  )

  val head2 = listOf(
    Pt(186, 236),
    Pt(179, 264),
    Pt(155, 279),
    Pt(133, 312),
    Pt(141, 331),
    Pt(159, 344),
    Pt(191, 358),
    Pt(474, 331),
    Pt(369, 342),
    Pt(288, 315),
    Pt(245, 274),
    Pt(213, 233),
    Pt(207, 257),
    Pt(153, 305),
    Pt(211, 247),
    Pt(234, 253),
  )

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

  val animatedHead:List<Pt> by derivedStateOf {
    val f = animatedFloat
    head1.mapIndexed { i, pt -> pt + (head2[i] - pt) * f }
  }

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

    val h00 by mkPt(animatedHead[0])
    val h01 by mkPt(animatedHead[1])
    val h02 by mkPt(animatedHead[2])
    val h03 by mkPt(animatedHead[3])
    val h04 by mkPt(animatedHead[4])
    val h05 by mkPt(animatedHead[5])
    val h06 by mkPt(animatedHead[6])
    val h32 by mkPt(animatedHead[7])
    val h33 by mkPt(animatedHead[8])
    val h34 by mkPt(animatedHead[9])
    val h35 by mkPt(animatedHead[10])
    val h36 by mkPt(animatedHead[11])
    val h37 by mkPt(animatedHead[12])
    val h41 by mkPt(animatedHead[13])
    val h42 by mkPt(animatedHead[14])
    val h43 by mkPt(animatedHead[15])

    val a = animatedPointsA
    drawCurve(0x44bb009900000000uL,listOf(h00,h01,h02,h03,h04,h05,h06,a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19],a[20],a[21],a[22],a[23],a[24],a[25],a[26],a[27],a[28],a[29],a[30],a[31],h32,h33,h34,h35,h36,h37,h00,), mapOf(a[10] to BR(a[38], a[39]),a[11] to BR(a[40], null),h02 to BR(h41, null),h36 to BR(h42, h43),a[27] to BR(null, a[44]),a[15] to BR(a[45], null),),)
    val b = animatedPointsB
    drawCurve(0x44bb00cc00000000uL,listOf(h00,h01,h02,h03,h04,h05,h06,b[7],b[8],b[9],b[10],b[11],b[12],b[13],b[14],b[15],b[16],b[17],b[18],b[19],b[20],b[21],b[22],b[23],b[24],b[25],b[26],b[27],b[28],b[29],b[30],b[31],h32,h33,h34,h35,h36,h37,h00,), mapOf(b[10] to BR(b[38], b[39]),b[11] to BR(b[40], null),h02 to BR(h41, null),h36 to BR(h42, h43),b[27] to BR(null, b[44]),b[15] to BR(b[45], null),),)
  }

}



