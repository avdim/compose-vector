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
  var animate: Boolean by remember { mutableStateOf(false) }
  val animatedInt by animateIntAsState(if (animate) 100 else 0)
  val animatedFloat by animateFloatAsState(if (animate) 1f else 0f)
//  val animatedPoints: List<Pt> by derivedStateOf { defaultPoints.map { it.copy(x = animatedInt) } }

//  val animatedPoints: List<Pt> by derivedStateOf {
//    val f = animatedFloat
//    start.mapIndexed { i, pt -> pt + (end[i] - pt) * f }
//  }

  GeneratedLayer(Modifier) {
//    val myPt by mkPt(140, 91)
    val p1 = Pt(423, 167)
    val p2 = Pt(427, 276)
    drawCurve(
      0xff0000ff00000000uL, listOf(p1, p2), bezierRef = mapOf(
        p1 to BezierRef(startRef = Pt(450, 211)),
        p2 to BezierRef(endRef = Pt(458, 240))
      )
    )
  }

//  Column {
//    TextButton("animate") {
//      animate = !animate
//    }
//    Text("${animatedInt}")
//  }

}
