package lib.vector

import com.squareup.kotlinpoet.*
import lib.vector.utils.base64

private const val USE_LIST = true

@OptIn(ExperimentalStdlibApi::class)
fun generateCode(elements: List<Element>, mapIdToPoint: Map<Id, Pt>): String {
//  val file = FileSpec.builder("com.uni", "GeneratedCode").addFunction(
  val idToIndex: MutableMap<Id, Int> = mutableMapOf()
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
        } else {
          if (!USE_LIST) {
            addStatement(buildString {
              append("val p${it.key.value} by mkPt(${it.value.x.toInt()}, ${it.value.y.toInt()})")
            })
          }
        }
      }
      if (USE_LIST) {
        addStatement(buildString {
          append("val l = listOf(")
          mapIdToPoint.entries.filter { it.key.name == null }
            .forEach {
              idToIndex[it.key] = idToIndex.size
              append("Pt(${it.value.x.toInt()}, ${it.value.y.toInt()}),")
            }
          append(")")
        })
      }
      elements.forEach { e ->
        addStatement(buildString {
          when (e) {
            is Element.Curve -> {
              append("drawCurve(")
              append("${e.color.literalStr},")
              append("listOf(")
              e.points.forEach {
                append("${it.constructorPtOrLink(mapIdToPoint, idToIndex)},")
              }
              append("),")
              append(" mapOf(")
              e.bezierRef.forEach {
                val keyStr = it.key.constructorPtOrLink(mapIdToPoint, idToIndex)
                val valueStr = it.value.constructorStr(mapIdToPoint, idToIndex)
                append("$keyStr to $valueStr,")
              }
              append("),")
              append(")")
            }
            is Element.Rectangle -> {
              append("drawRect(")
              append("${e.color.literalStr},")
              append("${e.start.constructorPtOrLink(mapIdToPoint, idToIndex)},")
              append("${e.end.constructorPtOrLink(mapIdToPoint, idToIndex)},")
              append(")")
            }
            is Element.Bitmap -> {
              append("drawBitmap(")
              append("${e.topLeft.constructorPtOrLink(mapIdToPoint, idToIndex)},")
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

private fun Id.constructorPtOrLink(map: Map<Id, Pt>, idToIndex:Map<Id, Int>): String =
  if (name != null) name else {
    if(USE_LIST) {
      "l[${idToIndex[this]}]"
    } else {
      "p${value}"
    }
//    pt(map).run { "Pt(${x.toInt()}, ${y.toInt()})" }
  }

private fun BezierRefEdit.constructorStr(map: Map<Id, Pt>, idToIndex:Map<Id, Int>): String {
  val startRefStr = startRef?.constructorPtOrLink(map, idToIndex)
  val endRefStr = endRef?.constructorPtOrLink(map, idToIndex)
  return "BR($startRefStr, $endRefStr)"
}

private val ULong.literalStr: String get() = "0x" + toString(radix = 16) + "uL"
