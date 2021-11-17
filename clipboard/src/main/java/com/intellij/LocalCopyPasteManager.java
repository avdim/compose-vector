// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class LocalCopyPasteManager implements ClientCopyPasteManager {

  public static final ClipboardSynchronizer CLIPBOARD_SYNCHRONIZER = new ClipboardSynchronizer();

  @Override
  public boolean areDataFlavorsAvailable(DataFlavor ... flavors) {
    return flavors.length > 0 && CLIPBOARD_SYNCHRONIZER.areDataFlavorsAvailable(flavors);
  }

  @Override
  public Transferable getContents() {
    return CLIPBOARD_SYNCHRONIZER.getContents();
  }


}
