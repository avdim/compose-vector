package com.uni

import androidx.compose.ui.graphics.Path
import com.uni.serializable.Pt

interface GeneratedScope {
  fun drawCurve(points: List<Pt>)
  fun drawRect(start: Pt, end: Pt)
}

