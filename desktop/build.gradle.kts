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
      }
    }
  }
}

compose.desktop {
  application {
    mainClass = "com.uni.MainKt"
  }
}
