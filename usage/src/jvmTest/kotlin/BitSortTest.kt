import kotlinx.coroutines.*
import org.junit.Test
import javax.script.ScriptContext
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BitSortTest {

  companion object {
    const val BLOCKS = 8
  }

  @Test
  fun testRandom() {
    fun checkInRandomArray() {
      val input = Array(1024 * 1024) {
        Random.nextInt(0, 1000)
      }
      val inputCopy = input.copyOf()
      val t1 = measureNanoTime {
        inputCopy.sort()
      }.also {
        println("quickSort: $it")
      }
      val t2 = measureNanoTime {
        bitSort(input)
      }.also {
        println("bitSort: $it")
      }
      println("ratio: ${t1 * 100 / t2 * 0.01}")
      assertTrue(inputCopy contentDeepEquals input)
    }
    repeat(10) {
      checkInRandomArray()
    }
  }

  fun bitSort(arr: Array<Int>) {
    runBlocking(context = Dispatchers.Default) {
      var blocks = BLOCKS
      fun blockSize(): Int {
        val result = arr.size / blocks
        return result
      }

      fun merge(l1: Int, r1: Int, l2: Int, r2: Int) {
        val beginCopy = Array(r1 - l1) { arr[l1 + it] }
        var firstPointer = 0
        var secondPointer = l2
        var insert = l1
        while (firstPointer < beginCopy.size && secondPointer < r2) {
          val first = beginCopy[firstPointer]
          val second = arr[secondPointer]
          if (first < second) {
            arr[insert] = first
            firstPointer++
          } else {
            arr[insert] = second
            secondPointer++
          }
          insert++
        }
        while (firstPointer < beginCopy.size) {
          arr[insert] = beginCopy[firstPointer]
          firstPointer++
          insert++
        }
      }
      coroutineScope {
        repeat(blocks) { i ->
          launch {
            arr.sort(i * blockSize(), (i + 1) * blockSize())
          }
//      sort(i * blockSize(), (i + 1) * blockSize())
        }
      }
      while (blocks > 1) {
        coroutineScope {
          blocks /= 2
          repeat(blocks) { i ->
            val size = blockSize()
            val begin = i * size
            val middle = i * size + size / 2
            val end = (i + 1) * size
            launch {
              merge(begin, middle, middle, end)
            }
          }
        }
      }
    }
  }

}
