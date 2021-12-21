plugins {
  kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
  id("org.jetbrains.compose")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
  jvm {
    withJava()
  }
  sourceSets {
    named("commonMain") {
      dependencies {
        api(project(":lib"))
//        api(compose.runtime)
//        api(compose.foundation)
//        api(compose.material)
      }
    }
    named("jvmMain") {
      dependencies {
        implementation(project(":clipboard"))
        implementation(compose.desktop.currentOs)
        implementation("com.squareup:kotlinpoet:1.10.2")
        implementation("org.pushing-pixels:radiance-animation:${RADIANCE_VERSION}")
        implementation("org.pushing-pixels:radiance-animation-ktx:${RADIANCE_VERSION}")
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
    mainClass = "com.usage.MainKt"
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}
