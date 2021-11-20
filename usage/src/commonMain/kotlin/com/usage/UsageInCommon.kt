package com.usage

import lib.vector.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {

  GeneratedLayer(Modifier) {
    val myPt by mkPt(140, 91)
    drawCurve(0xff0000ff00000000uL,listOf(myPt,Pt(181, 173),Pt(202, 185),Pt(215, 193),Pt(321, 270),Pt(376, 309),Pt(398, 324),Pt(408, 330),Pt(417, 334),Pt(431, 342),Pt(442, 346),Pt(453, 346),Pt(470, 339),Pt(481, 335),Pt(501, 331),Pt(523, 350),Pt(551, 344),Pt(580, 358),Pt(616, 349),Pt(646, 334),Pt(695, 300),Pt(615, 219),))
    drawRect(0xffffff0000000000uL,Pt(81, 405),Pt(411, 567),)
  }

}
