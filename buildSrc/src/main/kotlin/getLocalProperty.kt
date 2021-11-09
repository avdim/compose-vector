import org.gradle.api.Project
import java.io.File
import java.util.*

fun Project.getLocalProperty(key: String): String {
    fun printError() {
        val message = "ERROR! Please create local.properties with key $key"
        println(message)
        System.err.println(message)
    }

    val propertiesFile: File = rootProject.file("local.properties")
    val properties = Properties()
    if (propertiesFile.exists()) {
        properties.load(propertiesFile.inputStream())
        val value: String? = properties.getProperty(key)
        if (value != null) {
            return value
        } else {
            printError()
            return "error"
        }
    } else {
        printError()
        return "error"
    }
}
