plugins {
  kotlin("multiplatform") version "1.5.31" apply false
//  kotlin("plugin.serialization") version KOTLIN_VERSION apply false
  id("org.jetbrains.compose") version "1.0.0-beta5" apply false
}

//buildscript {
//  repositories {
//    google()
//  }
//}

allprojects {//todo allprojects bad?
//  buildDir = File("/dev/shm/$name")
//  version = "1.0"
  repositories {
    mavenLocal {
      url = uri("${rootProject.projectDir}/save_dependencies")
      // com/jetbrains/intellij/java/java-compiler-ant-tasks/211.7628.21.2111.7579519/java-compiler-ant-tasks-211.7628.21.2111.7579519.pom
    }
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
    //maven { setUrl("https://dl.bintray.com/kotlin/exposed") }
    maven { setUrl("https://kotlin.bintray.com/ktor") }
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()

//    if (USE_KOTLIN_DEV_REPOSITORY) {
//      maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
//      maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
//      maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
//      maven { setUrl("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
//    }
  }
  tasks.withType(AbstractTestTask::class) {
    testLogging {
      showStandardStreams = true
      events("passed", "failed")
    }
  }
  //todo check difference if use afterEvaluate { tasks... }
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
//    if (COMPOSE_WORKAROUND) {
//      kotlinOptions {
//        freeCompilerArgs += listOf("-P", "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true")
//      }
//    }
  }
}
