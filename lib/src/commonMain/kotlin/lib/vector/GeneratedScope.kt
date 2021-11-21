package lib.vector

import lib.vector.utils.*
import kotlin.reflect.KProperty

interface GeneratedScope {
  fun mkPt(x: Float, y: Float): MakePt
  fun drawCurve(color:ULong, points: List<Pt>, bezieRef:Map<Pt, BezierRef> = emptyMap())
  fun drawRect(color:ULong, start: Pt, end: Pt)
  fun drawBitmap(pt: Pt, byteArray: ByteArray)

  fun mkPt(x: Int, y: Int): MakePt = mkPt(x.toFloat(), y.toFloat())
  fun drawBitmap(pt: Pt, base64Str: String) = drawBitmap(pt, base64Str.fromBase64())
}

fun interface MakePt {
  operator fun getValue(noMatter: Any?, property: KProperty<*>): Pt
}
