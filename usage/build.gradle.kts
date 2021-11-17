plugins {
  kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
  id("org.jetbrains.compose")
}

kotlin {
  jvm {
    withJava()
  }
  sourceSets {
    named("commonMain") {
      dependencies {
        implementation(project(":lib"))
        api(compose.runtime)
        api(compose.foundation)
        api(compose.material)
      }
    }
    named("jvmMain") {
      dependencies {
        implementation(project(":clipboard"))
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}
