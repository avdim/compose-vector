plugins {
  kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
  id("org.jetbrains.compose")
}

kotlin {
  jvm {
    withJava()
  }
  sourceSets {
    named("jvmMain") {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation("com.squareup:kotlinpoet:1.10.2")
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.uni.MainKt"
  }
}
