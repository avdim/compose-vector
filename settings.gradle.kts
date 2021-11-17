pluginManagement {
  repositories {
    gradlePluginPortal()//todo alternative?: maven { setUrl("https://plugins.gradle.org/m2/") }
    mavenCentral()
//    maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
//    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
//    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
//    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") // plugin id("org.jetbrains.intellij") SNAPSHOT
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "org.jetbrains.compose" -> useModule("org.jetbrains.compose:compose-gradle-plugin:${requested.version}")
//        "kotlin-dce-js" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
//        "kotlinx-serialization" -> useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
//        "org.jetbrains.kotlin.multiplatform" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${target.version}")
      }
    }
  }
}
rootProject.name = "compose-vector"
//enableFeaturePreview("GRADLE_METADATA")
include("lib")
include("clipboard")
include("usage")
