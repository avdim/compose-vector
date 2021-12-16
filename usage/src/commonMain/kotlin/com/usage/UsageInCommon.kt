package com.usage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import lib.vector.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {
  val defaultPoints = listOf(Pt(376, 309), Pt(398, 324), Pt(408, 330), Pt(417, 334), Pt(431, 342))
  var animate: Boolean by remember { mutableStateOf(false) }
  val animatedInt by animateIntAsState(if (animate) 100 else 0)
  val animatedFloat by animateFloatAsState(if (animate) 1f else 0f)
//  val animatedPoints: List<Pt> by derivedStateOf { defaultPoints.map { it.copy(x = animatedInt) } }

//  val animatedPoints: List<Pt> by derivedStateOf {
//    val f = animatedFloat
//    start.mapIndexed { i, pt -> pt + (end[i] - pt) * f }
//  }

  GeneratedLayer(Modifier) {


    val keyPt1 by mkPt(397, 142)
    val l = listOf(Pt(160, 172),Pt(623, 403),Pt(207, 528),Pt(232, 150),Pt(322, 349),Pt(310, 265),)
    drawCurve(0xff0000ff00000000uL,listOf(l[0],keyPt1,l[1],l[2],l[3],), mapOf(keyPt1 to BezierRef(l[4], l[5]),),)
  }

//  Column {
//    TextButton("animate") {
//      animate = !animate
//    }
//    Text("${animatedInt}")
//  }

}
