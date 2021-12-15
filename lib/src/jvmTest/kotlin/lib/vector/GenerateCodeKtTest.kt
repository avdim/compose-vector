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
      val p2 = Pt(427, 276)
      drawCurve(
        0xff0000ff00000000uL, listOf(p1, p2), bezierRef = mapOf(
          p1 to BezierRef(startRef = Pt(450, 211)),
          p2 to BezierRef(endRef = Pt(458, 240))
        )
      )
    }
    assertEquals(
      """
        val p1 by mkPt(423, 167)
        drawCurve(0xff0000ff00000000uL,listOf(p1,Pt(427, 276),))
        
      """.trimIndent(),
      generateCode(state.savedElements, state.mapIdToPoint).also {
        println("------------------------------------")
        println(it)
        println("------------------------------------")
      }
    )
  }
}
