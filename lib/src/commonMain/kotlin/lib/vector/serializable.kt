package lib.vector

import lib.vector.utils.fromBase64

data class Pt(val x: Int, val y: Int)

sealed class Element {
  data class Curve(val color:ULong, val points: List<Pt>) : Element()
  data class Rect(val color:ULong, val start: Pt, val end: Pt) : Element()
  data class Bitmap(val x: Int, val y: Int, val byteArray: ByteArray) : Element() {
    constructor(x: Int, y: Int, base64Str: String) : this(x, y, base64Str.fromBase64())
  }
}
