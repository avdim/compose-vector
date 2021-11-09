import java.io.File

fun replaceInFile(file: String, from: Regex, to: String) {
  val f = File(file)
  f.writeText(f.readText().replace(from, to))
}

fun replaceInFile(file: String, from: String, to: String) {
  val f = File(file)
  f.writeText(f.readText().replace(from, to))
}
