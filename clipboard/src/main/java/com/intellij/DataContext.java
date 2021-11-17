// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@FunctionalInterface
public interface DataContext {
  /**
   * Returns the object corresponding to the specified data identifier. Some of the supported
   * data identifiers are defined in the {@link com.intellij.openapi.actionSystem.PlatformDataKeys} class.
   *
   * <b>NOTE:</b> For implementation only, prefer {@link DataContext#getData(DataKey)} in client code.
   *
   * @param dataId the data identifier for which the value is requested.
   * @return the value, or null if no value is available in the current context for this identifier.
   */
  @Nullable
  Object getData(@NotNull String dataId);

  DataContext EMPTY_CONTEXT = dataId -> null;

}
