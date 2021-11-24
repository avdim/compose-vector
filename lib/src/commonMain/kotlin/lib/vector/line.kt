package lib.vector

class LineSegment<T>(val before: T, val start: T, val end: T, val after: T)

fun <T,R> LineSegment<T>.map(lambda:(T)->R) = LineSegment<R>(
  before = lambda(before),
  start = lambda(start),
  end = lambda(end),
  after = lambda(after),
)

fun <T> List<T>.toLineSegments(): Collection<LineSegment<T>> =
  (this.takeOrSmaller(1) + this + this.takeLastOrSmaller(1)).windowed(4).map { (before, start, end, after) ->
    LineSegment(
      before = before, start = start, end = end, after = after
    )
  }

fun <T> List<T>.takeOrSmaller(n: Int) = take(minOf(n, size))
fun <T> List<T>.takeLastOrSmaller(n: Int) = takeLast(minOf(n, size))
