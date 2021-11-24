// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public abstract class CopyPasteManager {

  public static CopyPasteManager getInstance() {
    return new CopyPasteManagerEx();
  }

  public abstract boolean areDataFlavorsAvailable(DataFlavor... flavors);

  @Nullable
  public abstract Transferable getContents();

}
