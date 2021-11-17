package com.uni

import org.junit.Assert.*
import org.junit.Test

class GenerateCodeKtTest {
  @Test
  fun testSimple() {
    assertEquals(
      """ """,
      generateCode(
        listOf(
          Curve(
            listOf(
              Pt(0, 0), Pt(100, 100)
            )
          ),
          Curve(
            listOf(
              Pt(0, 0), Pt(100, 100)
            )
          ),
        )
      )
    )
  }
}
