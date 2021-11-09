package com.uni

import com.squareup.kotlinpoet.*

fun generateCode(curves: List<Curve>): String {
  val greeterClass = ClassName("", "Greeter")
  val file = FileSpec.builder("", "HelloWorld")
    .addType(
      TypeSpec.classBuilder("Greeter")
      .primaryConstructor(
        FunSpec.constructorBuilder()
        .addParameter("name", String::class)
        .build())
      .addProperty(
        PropertySpec.builder("name", String::class)
        .initializer("name")
        .build())
      .addFunction(FunSpec.builder("greet")
        .addStatement("println(%P)", "Hello, \$name")
        .build())
      .build())
    .addFunction(FunSpec.builder("main")
      .addParameter("args", String::class, KModifier.VARARG)
      .addStatement("%T(args[0]).greet()", greeterClass)
      .build())
    .build()

  return file.toString()
}
