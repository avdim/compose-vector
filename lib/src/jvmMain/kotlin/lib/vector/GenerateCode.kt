package lib.vector

import com.squareup.kotlinpoet.*
import lib.vector.utils.base64

@OptIn(ExperimentalStdlibApi::class)
fun generateCode(elements: List<Element>, mapIdToPoint: Map<Id, Pt>): String {

//  val file = FileSpec.builder("com.uni", "GeneratedCode").addFunction(
    val genFun = FunSpec.builder("generatedCode")
      .receiver(typeNameOf<GeneratedScope>())
//    .addParameter("args", String::class, KModifier.VARARG)
      .apply {
        elements.forEach { e ->
          addStatement(buildString {
            when(e) {
              is Element.Curve -> {
                append("drawCurve(")
                append("${e.color.literalStr},")
                append("listOf(")
                e.points.forEach {
                  append("${it.pt(mapIdToPoint).constructorStr},")
                }
                append(")")
                append(")")
              }
              is Element.Rect -> {
                append("drawRect(")
                append("${e.color.literalStr},")
                append("${e.start.pt(mapIdToPoint).constructorStr},")
                append("${e.end.pt(mapIdToPoint).constructorStr},")
                append(")")
              }
              is Element.Bitmap -> {
                append("drawBitmap(")
                append("${e.topLeft.pt(mapIdToPoint).constructorStr},")
                append("\"" + e.byteArray.base64 + "\"")
                append(")")
              }
            }
          })
        }
      }
      .build()
//  ).build()

  return genFun.body.toString()
//  return file.toString()
}

private val Pt.constructorStr: String
  get() = "Pt($x, $y)"

private val ULong.literalStr:String get() = "0x" + toString(radix = 16) + "uL"
