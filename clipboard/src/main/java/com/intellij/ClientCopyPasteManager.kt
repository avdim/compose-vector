// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij

import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

/**
 * A per-client service managing clipboard.
 * Take a look at [CopyPasteManagerEx]
 */

interface ClientCopyPasteManager {
  companion object {
    @JvmStatic
    fun getCurrentInstance(): ClientCopyPasteManager = LocalCopyPasteManager()
  }

  fun areDataFlavorsAvailable(vararg flavors: DataFlavor): Boolean
  fun getContents(): Transferable?
}