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
      mapIdToPoint.entries.forEach {
        val name = it.key.name
        if (name != null) {
          addStatement(buildString {
            append("val $name by mkPt(${it.value.x.toInt()}, ${it.value.y.toInt()})")
          })
        }
      }
      elements.forEach { e ->
        addStatement(buildString {
          when (e) {
            is Element.Curve -> {
              append("drawCurve(")
              append("${e.color.literalStr},")
              append("listOf(")
              e.points.forEach {
                append("${it.constructorPtOrLink(mapIdToPoint)},")
              }
              append("),")
              append(" mapOf(")
              e.bezierRef.forEach {
                val keyStr = it.key.constructorPtOrLink(mapIdToPoint)
                val valueStr = it.value.constructorStr(mapIdToPoint)
                append("$keyStr to $valueStr,")
              }
              append("),")
              append(")")
            }
            is Element.Rectangle -> {
              append("drawRect(")
              append("${e.color.literalStr},")
              append("${e.start.constructorPtOrLink(mapIdToPoint)},")
              append("${e.end.constructorPtOrLink(mapIdToPoint)},")
              append(")")
            }
            is Element.Bitmap -> {
              append("drawBitmap(")
              append("${e.topLeft.constructorPtOrLink(mapIdToPoint)},")
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

private fun Id.constructorPtOrLink(map: Map<Id, Pt>): String = if (name != null) name else pt(map).run { "Pt(${x.toInt()}, ${y.toInt()})" }
private fun BezierRefEdit.constructorStr(map: Map<Id, Pt>): String {
  val startRefStr = startRef?.constructorPtOrLink(map)
  val endRefStr = endRef?.constructorPtOrLink(map)
  return "BezierRef($startRefStr, $endRefStr)"
}

private val ULong.literalStr: String get() = "0x" + toString(radix = 16) + "uL"
