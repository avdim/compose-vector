package com.uni

import com.squareup.kotlinpoet.*

@OptIn(ExperimentalStdlibApi::class)
fun generateCode(curves: List<Curve>): String {

//  val file = FileSpec.builder("com.uni", "GeneratedCode").addFunction(
    val genFun = FunSpec.builder("generatedCode")
      .receiver(typeNameOf<GeneratedScope>())
//    .addParameter("args", String::class, KModifier.VARARG)
      .apply {
        curves.forEach {
          addStatement(buildString {
            append("drawCurve(")
            append("listOf(")
            it.points.forEach {
              with(it) {
                append("Pt($x, $y),")
              }
            }
            append(")")
            append(")")
          })
        }
      }
      .build()
//  ).build()

  return genFun.body.toString()
//  return file.toString()
}
