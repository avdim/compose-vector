package lib.vector

import com.uni.generateCode
import org.junit.Assert.*
import org.junit.Test

class GenerateCodeKtTest {
  @Test
  fun testSimple() {
    assertEquals(
      """ """,
      generateCode(
        listOf(
          Element.Curve(
            listOf(
              Pt(0, 0), Pt(100, 100)
            )
          ),
          Element.Curve(
            listOf(
              Pt(0, 0), Pt(100, 100)
            )
          ),
        )
      )
    )
  }
}
