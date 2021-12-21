import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.usage.runSimpleComposableWindow

private data class Movie(
  val id: Int,
  val name: String
)

@Composable
private fun MoviesScreen(movies: List<Movie>) {
  Column {
    for (movie in movies) {
      key(movie.id) { // Unique ID for this movie
        MovieOverview(movie)
      }
    }
  }
}

@Composable
private fun MovieOverview(movie: Movie) {
  Text("Movie ${movie.name}")
}

fun main() {
  runSimpleComposableWindow {
    MoviesScreen(
      listOf(
        Movie(1, "Godzila"),
        Movie(2, "Kong"),
      )
    )
  }
}
