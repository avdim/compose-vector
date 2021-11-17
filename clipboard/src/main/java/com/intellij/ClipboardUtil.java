// Copyright 2000-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.DataFlavor;
import java.util.function.Supplier;

public final class ClipboardUtil {
  private static final Log LOG = new Log();

  public static <E> E handleClipboardSafely(@NotNull Supplier<? extends E> supplier, E defaultValue) {
    try {
      return supplier.get();
    }
    catch (IllegalStateException e) {
      if (SystemInfo.isWindows) {
        LOG.debug("Clipboard is busy");
      }
      else {
        LOG.warn(e);
      }
    }
    catch (NullPointerException e) {
      LOG.warn("Java bug #6322854", e);
    }
    catch (IllegalArgumentException e) {
      LOG.warn("Java bug #7173464", e);
    }

    return defaultValue;
  }

//  public static @Nullable String getTextInClipboard() {
//    return CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
//  }
}
