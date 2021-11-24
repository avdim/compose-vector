// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class CopyPasteManagerEx extends CopyPasteManager {

  @Override
  public boolean areDataFlavorsAvailable(DataFlavor... flavors) {
    return ClientCopyPasteManager.getCurrentInstance().areDataFlavorsAvailable(flavors);
  }

  @Override
  public @Nullable
  Transferable getContents() {
    return ClientCopyPasteManager.getCurrentInstance().getContents();
  }

}
