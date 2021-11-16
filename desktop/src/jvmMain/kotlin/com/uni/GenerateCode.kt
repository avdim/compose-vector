package com.uni

import com.squareup.kotlinpoet.*
import com.uni.serializable.Element
import com.uni.serializable.Pt

@OptIn(ExperimentalStdlibApi::class)
fun generateCode(elements: List<Element>): String {

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
                append("listOf(")
                e.points.forEach {
                  append("${it.constructorStr},")
                }
                append(")")
                append(")")
              }
              is Element.Rect -> {
                append("drawRect(")
                append("${e.start.constructorStr},")
                append("${e.end.constructorStr},")
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
