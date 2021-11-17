plugins {
  id("com.android.library")
  kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
  id("org.jetbrains.compose")
}

kotlin {
  jvm()
  android()
  sourceSets {
    named("commonMain") {
      dependencies {
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
    named("androidMain") {
      dependencies {
        api("androidx.appcompat:appcompat:1.3.1")//todo move to buildSrc
        api("androidx.core:core-ktx:1.3.1")
      }
    }
    named("jvmTest") {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }
}

android {
  compileSdk = 31

  defaultConfig {
    minSdk = 21
    targetSdk = 31
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  sourceSets {
    named("main") {
      manifest.srcFile("src/androidMain/AndroidManifest.xml")
      res.srcDirs("src/androidMain/res")
    }
  }
}
