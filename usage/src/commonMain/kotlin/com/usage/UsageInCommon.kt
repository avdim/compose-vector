package com.usage

import lib.vector.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lib.vector.Pt

@Composable
fun UsageInCommon() {

  GeneratedLayer(Modifier) {
    val myPt:Pt by mkPt(200,200)
    println(myPt)

    drawCurve(0xff0000ff00000000uL,listOf(Pt(175, 170),Pt(181, 173),Pt(202, 185),Pt(215, 193),Pt(321, 270),Pt(376, 309),Pt(398, 324),Pt(408, 330),Pt(417, 334),Pt(431, 342),Pt(442, 346),Pt(453, 346),Pt(470, 339),Pt(481, 335),Pt(499, 328),Pt(511, 324),Pt(518, 321),Pt(521, 319),Pt(530, 308),Pt(533, 302),Pt(537, 290),Pt(538, 284),))


  }

}
