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

    val p1 by mkPt(205, 340)
    val p2 by mkPt(442, 180)
    val p3 by mkPt(478, 425)
    val p4 by mkPt(208, 345)
    val p5 by mkPt(357, 324)
    val p6 by mkPt(349, 347)
    val p7 by mkPt(340, 509)
    val p8 by mkPt(267, 179)
    val p9 by mkPt(562, 233)
    val p10 by mkPt(381, 316)
    val p11 by mkPt(470, 300)
    val p12 by mkPt(478, 335)
    drawCurve(0xff0000ff00000000uL,listOf(p1,p2,p10,p3,p4,), mapOf(p1 to BezierRef(p5, null),p4 to BezierRef(null, p6),p3 to BezierRef(p7, null),p2 to BezierRef(p9, p8),p10 to BezierRef(p12, p11),),)


  }

//  Column {
//    TextButton("animate") {
//      animate = !animate
//    }
//    Text("${animatedInt}")
//  }

}
