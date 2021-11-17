package lib.vector

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

fun pasteToClipboard(result: String) {
  println(result)
  val stringSelection = StringSelection(result)
  val clipboard: Clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
  clipboard.setContents(stringSelection, null)
}
