import org.gradle.api.Project
import java.text.SimpleDateFormat
import java.util.*

val UNI_VERSION = "0.13.0"
val BUILD_TIME_STR = SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale("ru", "RU")).format(Date())
//val BUILD_TIME_STR = Date().toString()

/**
 * Отладочная версия JVM схожа с JS. Можно выставлять не зависимо от JS.
 * Но если игровая логика содержит проверки IS_DEBUG, или DEBUG{...}, то возможна рассинхронизация стейта итгры.
 */
val DEBUG_JVM = true//todo false

val MIN_JDK_VERSION: JdkVersion = JdkVersion.JDK11
val JVM_TARGET = MIN_JDK_VERSION.kotlinTarget

/**
 * Если хочется потестировать EAP или DEV версии kotlin.
 * Dev релизы на свой страх и риск: https://dl.bintray.com/kotlin/kotlin-dev/org/jetbrains/kotlin/kotlin-gradle-plugin/
 */
val USE_KOTLIN_DEV_REPOSITORY = false

//val KOTLIN_VERSION = "1.4.32"
//val KOTLIN_VERSION = "1.5.10"
//val KOTLIN_VERSION = "1.5.21"
val KOTLIN_VERSION = "1.5.31"
val SERIALIZATION_VERSION = "1.0.1"
val COROUTINE_VERSION = "1.4.2"
val KTOR_VERSION = "1.5.0"
val LOG_MAVEN_ARTIFACT = if (DEBUG_JVM) "ch.qos.logback:logback-classic:1.2.3" else "org.slf4j:slf4j-simple:1.7.28"
val GMAZZO_BUILDCONFIG_VERSION = "3.0.1"
val RADIANCE_VERSION = "5.0.0"

//https://github.com/Kotlin/kotlinx.coroutines/commit/e37aec4edd09bfb7f622e113553aa88a0a5bd27c
val COMPILER_ARGS = listOf<String>()
//val COMPILER_ARGS = listOf<String>("-Xir-produce-js", "-Xgenerate-dts", "-XXLanguage:+NewInference")
//tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
//        kotlinOptions {
//            freeCompilerArgs += COMPILER_ARGS
//or in gradle.properties: kotlin.js.compiler=ir

// https://www.jetbrains.com/intellij-repository/snapshots/
//val LAST_IDEA_STR = "2021.1.3"
val LAST_IDEA_STR = "2021.2.2"
//val LAST_IDEA_STR = "213.3714.440-EAP-SNAPSHOT"
//val LAST_IDEA_STR = "213.4293.20-EAP-SNAPSHOT"
//val LAST_IDEA_STR = "213.4631.20-EAP-SNAPSHOT"//2021.3-eap3
//val LAST_IDEA_STR = "213.4928-EAP-CANDIDATE-SNAPSHOT"
//val LAST_IDEA_STR = "213.5449-EAP-CANDIDATE-SNAPSHOT"

val LAST_COMMUNITY = IdeaVersion.Download(LAST_IDEA_STR, "IC")
val LAST_ULTIMATE = IdeaVersion.Download(LAST_IDEA_STR, "IU")

// https://github.com/JetBrains/gradle-intellij-plugin
//val INTELLIJ_GRADLE = "1.2.0"
val INTELLIJ_GRADLE = "1.2.1"
// https://maven.pkg.jetbrains.space/public/p/compose/dev/org/jetbrains/compose/org.jetbrains.compose.gradle.plugin/
//val DESKTOP_COMPOSE = "1.0.0-alpha4-build362"
//val DESKTOP_COMPOSE = "1.0.0-alpha4-build396"
val DESKTOP_COMPOSE = "1.0.0-beta5" //todo поправить resolution strategy в settings.gradle.kts
val COMPOSE_WORKAROUND = true

//val asMac = "/Users/dim/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/203.7185775/Android Studio Preview.app/Contents"
//val asMac = "/Users/dim/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/203.7360992/Android Studio Preview.app/Contents"
//val asMac = "/Users/dim/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-1/203.7717.56.2111.7361063/Android Studio Preview.app/Contents"
//val asMac = "/Users/dim/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/212.4037.9.2112.7818732/Android Studio Preview.app/Contents"
val asMac = "/Users/dim/Desktop/android-studio/2021.2/android-studio-2021.2.1.1-mac-canary1/Contents"
//val asLinux = "/home/dim/Desktop/programs/android-studio-4.2/2020.3.1.8"
//val asLinux = "/home/dim/Desktop/android_studio/2020.3.1.1_canary10/"
//val asLinux = "/home/dim/Desktop/android_studio/2020.3_alpha12/extracted"
//val asLinux = "/home/dim/Desktop/android_studio/2020.3_beta1/android-studio"
//val asLinux = "/home/dim/Desktop/android_studio/2020.3.1/android-studio"
//val asLinux = "/home/dim/Desktop/android_studio/2021_alpha1/android-studio"
//val asLinux = "/home/dim/Desktop/android_studio/2021_canary5/android-studio/"
//val asLinux = "/home/dim/Desktop/android_studio/2021_canary8/android-studio/"
//val asLinux = "/home/dim/Desktop/android_studio/2021_canary9/android-studio/"
val asLinux = "/home/dim/Desktop/android_studio/2021.2_canary2/android-studio/"
//val asLinux = "/home/dim/Desktop/android_studio/2021.2_canary3/android-studio/"

val Project.UNI_BUILD_TYPE: BuildType get() =
  when (safeArgument("uniBuildType")) {
    "release" -> BuildType.Release
    "as" -> if (isMacOS) {
      BuildType.UseLocal(asMac)
    } else {
      BuildType.UseLocal(asLinux)
    }
    "integration-test" -> BuildType.IntegrationTest
    "hand-test" -> BuildType.HandTest
    else -> BuildType.Debug
  }

val Project.myIdeaSandboxDir: String
  get() = UNI_BUILD_TYPE.let { buildType ->
    when (buildType) {
      BuildType.Release, BuildType.Debug, BuildType.HandTest -> {
        //HOME_DIR.resolve("Desktop/uni_release_system").absolutePath
        //tmpDir()//"/tmp/idea_sandbox"
        val file = projectDir.resolve(".exclude/.idea_system_${IDEA_VERSION.postfixName}")
        file.mkdirs()
        file.absolutePath
      }
      is BuildType.UseLocal -> {
        val file = projectDir.resolve(".exclude/.idea_system_local_${buildType.path.hashCode()}")
        file.mkdirs()
        file.absolutePath
      }
      BuildType.IntegrationTest -> tmpDir()
    }
  }

val Project.myIdeaDependencyCachePath: String
  get() =
    rootProject.projectDir.resolve(".exclude").resolve("my_idea_dependency_cache_path").absolutePath

val Project.IDEA_VERSION: IdeaVersion get() = UNI_BUILD_TYPE.let {buildType->
  when (buildType) {
    is BuildType.Debug, BuildType.HandTest -> {
      LAST_COMMUNITY
    }
    is BuildType.Release -> {
      LAST_ULTIMATE
    }
    is BuildType.IntegrationTest -> {
      LAST_COMMUNITY
    }
    is BuildType.UseLocal -> {
      IdeaVersion.Local(buildType.path)
    }
  }
}

fun Project.safeArgument(key: String): String? =
  if (hasProperty(key)) {
    property(key) as? String
  } else {
    null
  }

val isMacOS get() = System.getProperty("os.name")?.contains("Mac OS") ?: false
