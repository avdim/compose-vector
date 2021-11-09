import java.io.File

sealed class IdeaVersion {
  abstract val type: String?

  class Download(val version: String, override val type: String = "IC") : IdeaVersion()

  class Local(val localPath: String) : IdeaVersion() {
    override val type: String? = null
  }

}

val IdeaVersion.postfixName: String get() =
  when(this) {
    is IdeaVersion.Download -> "$version-$type".toLowerCase()
    is IdeaVersion.Local -> {
      val shortPath = localPath.split(File.pathSeparator).map { it.take(3) }.joinToString("-")
      "local-$type-$shortPath".toLowerCase()
    }
  }
