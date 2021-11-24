// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij

import java.awt.Image
import java.awt.datatransfer.DataFlavor.imageFlavor
import java.awt.image.BufferedImage
import java.awt.image.MultiResolutionImage
import java.io.IOException

/**
 * Represents a basic paste provider that allows to paste screenshots (from clipboard) as PNG files.
 *
 * NOTE: If registered as `filePasteProvider` handles paste operations in project view.
 */
open class ImagePasteProvider : PasteProvider {
  final override fun isPastePossible(dataContext: DataContext): Boolean = true
  final override fun isPasteEnabled(dataContext: DataContext): Boolean =
    CopyPasteManager.getInstance().areDataFlavorsAvailable(imageFlavor)
      && isEnabledForDataContext(dataContext)
//    && dataContext.getData(CommonDataKeys.VIRTUAL_FILE) != null

  open fun isEnabledForDataContext(dataContext: DataContext): Boolean = true

  final override fun performPaste(dataContext: DataContext) {
    val pasteContents = CopyPasteManager.getInstance().contents ?: return
    // Step 1: Obtain image data from the clipboard
    val imageToPaste: BufferedImage? = try {
      pasteContents.getTransferData(imageFlavor)
    } catch (ioException: IOException) {
      //todo catch IOException
      ioException.printStackTrace()
      println("Failed to get data from the clipboard. Data is no longer available. Aborting operation.")
      return
    }.let {
      when (it) {
        is MultiResolutionImage -> it.resolutionVariants.firstOrNull()?.toBufferedImage()
        is BufferedImage -> it
        is Image -> it.toBufferedImage()
        else -> null
      }
    }

  }

}

fun Image.toBufferedImage() = let { img ->
  when (img) {
    is BufferedImage -> img
    else -> {
      // Create a buffered image with transparency
      val bufferedImage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

      // Draw the image on to the buffered image
      val bGr = bufferedImage.createGraphics()
      bGr.drawImage(img, 0, 0, null)
      bGr.dispose()

      bufferedImage
    }
  }
}

fun getClipboardImage(): BufferedImage? {
  val pasteContents = CopyPasteManager.getInstance().contents ?: return null
  // Step 1: Obtain image data from the clipboard
  val imageToPaste: BufferedImage? = try {
    pasteContents.getTransferData(imageFlavor)
  } catch (ioException: IOException) {
    //todo catch IOException
    ioException.printStackTrace()
    println("Failed to get data from the clipboard. Data is no longer available. Aborting operation.")
    return null
  }.let {
    when (it) {
      is MultiResolutionImage -> it.resolutionVariants.firstOrNull()?.toBufferedImage()
      is BufferedImage -> it
      is Image -> it.toBufferedImage()
      else -> null
    }
  }
  return imageToPaste?.toBufferedImage()
}
