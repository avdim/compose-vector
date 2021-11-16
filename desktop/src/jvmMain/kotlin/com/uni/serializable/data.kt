package com.uni.serializable

data class Pt(val x: Int, val y: Int)

sealed class Element {
  data class Curve(val points: List<Pt>) : Element()
  data class Rect(val start: Pt, val end: Pt) : Element()
}
