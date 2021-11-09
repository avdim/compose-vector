import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class SplitByLayersTask : DefaultTask() {

  @InputDirectory
  lateinit var inputDir: File

//  @get:OutputDirectory
  val outputDir
    get() = File(inputDir.absolutePath + "_layers")

  private val layerFilters: MutableList<(LayerFilterData) -> Boolean> = mutableListOf()

  fun layer(lambda: (LayerFilterData) -> Boolean) {
    layerFilters.add(lambda)
  }

  @TaskAction
  fun process() {
    println("process SplitByLayersTask, inputDir: ${inputDir.absolutePath}, ${inputDir.exists()}")
    outputDir.deleteRecursively()

    fun recursive(relativePath: String) {
      inputDir.resolve(relativePath)
        .listFiles().orEmpty().asSequence()
        .filter { it.name != "." && it.name != ".." }
        .forEach { f->
          if (f.isDirectory) {
            recursive(relativePath + "/" + f.name)
          } else {
            val layer = 1 + layerFilters.indexOfLast {
              it(LayerFilterData(relativePath, f))
            }
            f.copyTo(outputDir.resolve("$layer/$relativePath").resolve(f.name), true)
          }
        }
    }
    recursive(".")
  }

}

class LayerFilterData(val relativePath: String, val file: File)
