package lib.vector

import androidx.compose.ui.Modifier
import org.junit.Assert.*
import org.junit.Test

class GenerateCodeKtTest {
  @Test
  fun testSimple() {
    val state = initializeByGeneratedScope {
      //    val myPt by mkPt(140, 91)
      val p1 by mkPt(423, 167)
      val p2 by mkPt(427, 276)
      val p3 by mkPt(450, 211)
      drawCurve(
        0xff0000ff00000000uL, listOf(p1, p2), mapOf(
          p1 to BezierRef(p3, null),
          p2 to BezierRef(null, Pt(458, 240))
        )
      )
    }
    assertEquals(
      """
        val p1 by mkPt(423, 167)
        val p2 by mkPt(427, 276)
        val p3 by mkPt(450, 211)
        drawCurve(0xff0000ff00000000uL,listOf(p1,p2,), mapOf(p1 to BezierRef(p3, null),p2 to BezierRef(null, Pt(458, 240)),),)
        
      """.trimIndent(),
      generateCode(state.savedElements, state.mapIdToPoint).also {
        println("------------------------------------")
        println(it)
        println("------------------------------------")
      }
    )
  }
}
