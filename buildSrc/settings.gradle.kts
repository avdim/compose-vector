
//MASSPOWER OLD:
//pluginManagement {
//  repositories {
////    mavenCentral()
////    maven { setUrl("https://jcenter.bintray.com/") }
//    gradlePluginPortal()
//    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
//    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
//  }
//
//  resolutionStrategy {
//    eachPlugin {
//      when (requested.id.id) {
//        "org.jetbrains.kotlin.multiplatform" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${target.version}")
//      }
//    }
//  }
//}


// OLD:
//pluginManagement {
//    repositories {
////    mavenCentral()
////    maven { setUrl("https://jcenter.bintray.com/") }
//        gradlePluginPortal()
//    }
//}

//include("lib")
//
//if(false) {
//  fun includeBuild2(relativePath: String) {
//    val dirName = relativePath.split("/").last()
//    include(":$dirName")
//    project(":$dirName").projectDir = file(relativePath)
//  }
//  includeBuild2("../lib")
//}
//
