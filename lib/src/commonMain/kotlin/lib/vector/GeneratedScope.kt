package lib.vector

import lib.vector.utils.*

interface GeneratedScope {
  fun drawCurve(points: List<Pt>)
  fun drawRect(start: Pt, end: Pt)
  fun drawBitmap(x:Int, y:Int, byteArray: ByteArray)
  fun drawBitmap(x: Int, y: Int, base64Str: String) = drawBitmap(x, y, base64Str.fromBase64())
}
