plugins {
  kotlin("multiplatform") version KOTLIN_VERSION apply false
//  kotlin("plugin.serialization") version KOTLIN_VERSION apply false
  id("org.jetbrains.compose") version COMPOSE_VERSION apply false
}

buildscript {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  dependencies {
    classpath("org.jetbrains.compose:compose-gradle-plugin:1.0.0-beta5")
    classpath("com.android.tools.build:gradle:4.1.3")
  }
}

allprojects {
  repositories {
    google()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
  tasks.withType(AbstractTestTask::class) {
    testLogging {
      showStandardStreams = true
      events("passed", "failed")
    }
  }
}
