package com.usage

import androidx.compose.animation.core.*
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import lib.vector.Pt
import kotlin.random.Random

@Composable
fun UsageInCommon() {
  val points1 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(223, 407),Pt(214, 471),Pt(162, 518),Pt(132, 572),Pt(203, 533),Pt(283, 492),Pt(365, 478),Pt(446, 473),Pt(583, 473),Pt(667, 502),Pt(713, 536),Pt(732, 572),Pt(765, 577),Pt(765, 533),Pt(733, 490),Pt(701, 445),Pt(658, 379),Pt(626, 340),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(160, 575),Pt(111, 532),Pt(232, 519),Pt(167, 285),Pt(232, 228),Pt(247, 233),Pt(607, 190),Pt(641, 486),)
  val points2 = listOf(Pt(205, 211),Pt(201, 242),Pt(169, 259),Pt(147, 292),Pt(155, 311),Pt(173, 324),Pt(205, 338),Pt(245, 431),Pt(310, 474),Pt(331, 538),Pt(309, 597),Pt(368, 564),Pt(377, 509),Pt(392, 472),Pt(454, 468),Pt(501, 475),Pt(535, 510),Pt(519, 548),Pt(487, 575),Pt(520, 592),Pt(552, 571),Pt(581, 536),Pt(598, 484),Pt(624, 425),Pt(631, 342),Pt(605, 265),Pt(581, 208),Pt(596, 166),Pt(560, 179),Pt(559, 243),Pt(580, 297),Pt(562, 326),Pt(489, 329),Pt(385, 336),Pt(304, 305),Pt(257, 256),Pt(239, 214),Pt(225, 237),Pt(365, 601),Pt(289, 568),Pt(385, 536),Pt(167, 285),Pt(232, 228),Pt(247, 233),Pt(607, 190),Pt(528, 488),)

  val head1 = listOf(Pt(205, 211), Pt(196, 243), Pt(169, 259), Pt(147, 292), Pt(155, 311), Pt(173, 324), Pt(205, 338), Pt(489, 329), Pt(385, 336), Pt(304, 305), Pt(257, 256), Pt(239, 214), Pt(225, 237), Pt(167, 285), Pt(229, 227), Pt(247, 233),)
  val head2 = listOf(Pt(177, 256),Pt(175, 282),Pt(151, 297),Pt(129, 330),Pt(137, 349),Pt(155, 362),Pt(187, 376),Pt(454, 322),Pt(355, 352),Pt(284, 333),Pt(241, 292),Pt(206, 251),Pt(203, 275),Pt(149, 323),Pt(207, 265),Pt(230, 271))

  val tail1 = listOf(Pt(605, 265), Pt(581, 208), Pt(596, 166), Pt(560, 179), Pt(559, 243), Pt(580, 297), Pt(553, 325), Pt(607, 190))
  val tail2 = listOf(Pt(657, 308), Pt(696, 280), Pt(747, 264), Pt(714, 241), Pt(651, 267), Pt(601, 296), Pt(553, 325), Pt(716, 271))

  val infiniteTransition = rememberInfiniteTransition()
  val legsAnimationRadio by infiniteTransition.animateFloat(
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
  val headAnimationRadio by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = keyframes {
        durationMillis = 1500
        0.3f at 300
        0.7f at 700
      },
      repeatMode = RepeatMode.Reverse
    )
  )

  var tailAnimationRadio by remember { mutableStateOf(0f) }

  LaunchedEffect(Unit) {
    while(true) {
      withFrameNanos { it }
      tailAnimationRadio = Random.nextDouble(0.0, 1.0).toFloat()
    }
  }

//  val tailAnimationRadio by infiniteTransition.animateFloat(
//    initialValue = 0f,
//    targetValue = 1f,
//    animationSpec = infiniteRepeatable(
//      animation = keyframes {
//        durationMillis = 1600
////        0.3f at 200
////        0.7f at 600
//      },
//      repeatMode = RepeatMode.Reverse
//    )
//  )

  val animatedHead:List<Pt> by derivedStateOf {
    val f = headAnimationRadio
    head1.mapIndexed { i, pt -> pt + (head2[i] - pt) * f }
  }

  val animatedTail:List<Pt> by derivedStateOf {
    val f = tailAnimationRadio
    tail2.mapIndexed { i, pt -> pt + (tail1[i] - pt) * f }
  }

  val animatedPointsA: List<Pt> by derivedStateOf {
    val f = legsAnimationRadio
    points1.mapIndexed { i, pt -> pt + (points2[i] - pt) * f }
  }
  val animatedPointsB: List<Pt> by derivedStateOf {
    val f = legsAnimationRadio
    points2.mapIndexed { i, pt -> pt + (points1[i] - pt) * f }
  }
//  CatBitmap()

  GeneratedLayer(Modifier) {

    val h00 = animatedHead[0]
    val h01 = animatedHead[1]
    val h02 = animatedHead[2]
    val h03 = animatedHead[3]
    val h04 = animatedHead[4]
    val h05 = animatedHead[5]
    val h06 = animatedHead[6]
    val h32 = animatedHead[7]
    val h33 = animatedHead[8]
    val h34 = animatedHead[9]
    val h35 = animatedHead[10]
    val h36 = animatedHead[11]
    val h37 = animatedHead[12]
    val h41 = animatedHead[13]
    val h42 = animatedHead[14]
    val h43 = animatedHead[15]

    val t25 by mkPt(animatedTail[0])
    val t26 by mkPt(animatedTail[1])
    val t27 by mkPt(animatedTail[2])
    val t28 by mkPt(animatedTail[3])
    val t29 by mkPt(animatedTail[4])
    val t30 by mkPt(animatedTail[5])
    val t31 by mkPt(animatedTail[6])
    val t44 by mkPt(animatedTail[7])

    val a = animatedPointsA
    drawCurve(0x55bb009900000000uL,listOf(h00,h01,h02,h03,h04,h05,h06,a[7],a[8],a[9],a[10],a[11],a[12],a[13],a[14],a[15],a[16],a[17],a[18],a[19],a[20],a[21],a[22],a[23],a[24],t25,t26,t27,t28,t29,t30,t31,h32,h33,h34,h35,h36,h37,h00,), mapOf(a[10] to BR(a[38], a[39]),a[11] to BR(a[40], null),h02 to BR(h41, null),h36 to BR(h42, h43),t27 to BR(null, t44),a[15] to BR(a[45], null),),)
    val b = animatedPointsB
    drawCurve(0x55bb00cc00000000uL,listOf(h00,h01,h02,h03,h04,h05,h06,b[7],b[8],b[9],b[10],b[11],b[12],b[13],b[14],b[15],b[16],b[17],b[18],b[19],b[20],b[21],b[22],b[23],b[24],t25,t26,t27,t28,t29,t30,t31,h32,h33,h34,h35,h36,h37,h00,), mapOf(b[10] to BR(b[38], b[39]),b[11] to BR(b[40], null),h02 to BR(h41, null),h36 to BR(h42, h43),t27 to BR(null, t44),b[15] to BR(b[45], null),),)
  }

}
