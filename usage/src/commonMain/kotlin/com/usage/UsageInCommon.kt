package com.usage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val defaultPoints = listOf(Pt(376, 309), Pt(398, 324), Pt(408, 330), Pt(417, 334), Pt(431, 342))
  var animate:Boolean by remember { mutableStateOf(false) }
  val animatedInt by animateIntAsState(if (animate) 100 else 0)
  val animatedFloat by animateFloatAsState(if (animate) 1f else 0f)
//  val animatedPoints: List<Pt> by derivedStateOf { defaultPoints.map { it.copy(x = animatedInt) } }

  val start = listOf(Pt(134, 553),Pt(137, 551),Pt(142, 547),Pt(147, 541),Pt(157, 526),Pt(161, 518),Pt(168, 501),Pt(170, 493),Pt(169, 471),Pt(166, 462),Pt(157, 442),Pt(153, 432),Pt(138, 414),Pt(118, 387),Pt(110, 378),Pt(104, 369),Pt(98, 360),Pt(85, 340),Pt(74, 318),Pt(70, 306),Pt(68, 295),Pt(65, 261),Pt(67, 238),Pt(69, 226),Pt(72, 214),Pt(78, 203),Pt(91, 187),Pt(108, 176),Pt(117, 173),Pt(126, 170),Pt(137, 170),Pt(159, 174),Pt(182, 185),Pt(193, 194),Pt(200, 203),Pt(207, 214),Pt(214, 227),Pt(226, 256),Pt(232, 287),Pt(236, 304),Pt(238, 321),Pt(241, 337),Pt(247, 371),Pt(252, 406),Pt(254, 425),Pt(257, 442),Pt(263, 475),Pt(274, 500),Pt(280, 511),Pt(285, 520),Pt(307, 549),Pt(315, 557),Pt(336, 572),Pt(350, 579),Pt(364, 586),Pt(402, 601),Pt(420, 609),Pt(420, 609),)
  val end = listOf(Pt(134, 553),Pt(137, 551),Pt(142, 547),Pt(147, 541),Pt(157, 526),Pt(161, 518),Pt(168, 501),Pt(170, 493),Pt(169, 471),Pt(166, 462),Pt(157, 442),Pt(153, 432),Pt(138, 414),Pt(118, 387),Pt(110, 378),Pt(118, 367),Pt(112, 357),Pt(115, 345),Pt(118, 328),Pt(115, 315),Pt(113, 300),Pt(110, 286),Pt(114, 277),Pt(121, 264),Pt(129, 254),Pt(141, 237),Pt(154, 212),Pt(167, 183),Pt(184, 157),Pt(223, 146),Pt(267, 134),Pt(301, 140),Pt(337, 146),Pt(381, 155),Pt(415, 177),Pt(419, 204),Pt(412, 236),Pt(406, 265),Pt(396, 285),Pt(379, 310),Pt(361, 323),Pt(328, 338),Pt(303, 363),Pt(280, 397),Pt(254, 425),Pt(257, 442),Pt(263, 475),Pt(274, 500),Pt(280, 511),Pt(285, 520),Pt(307, 549),Pt(315, 557),Pt(336, 572),Pt(350, 579),Pt(364, 586),Pt(402, 601),Pt(420, 609),Pt(420, 609),)
  val animatedPoints: List<Pt> by derivedStateOf {
    val f = animatedFloat
    start.mapIndexed { i, pt -> pt + (end[i] - pt) * f }
  }

  GeneratedLayer(Modifier) {
    val myPt by mkPt(140, 91)

    drawCurve(0xff0000ff00000000uL, animatedPoints)
  }

  Column {
    TextButton("animate") {
      animate = !animate
    }
    Text("${animatedInt}")
  }

}
