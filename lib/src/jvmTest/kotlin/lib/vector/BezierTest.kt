package lib.vector

import org.junit.Assert
import org.junit.Test

class BezierTest {
  @Test
  fun testSubSegment() {
    val segment = BezierSegment(Pt(0, 0), Pt(100, 100), Pt(0, 0), Pt(100, 100))
    val firstSplit = segment.split(0.5f).first
    val subSegment = segment.subSegment(0.0f, 0.5f)
    Assert.assertEquals(firstSplit, subSegment)
  }
}
