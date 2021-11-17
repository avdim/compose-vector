// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import java.util.Locale;

public final class SystemInfoRt {
  public static final String OS_NAME = System.getProperty("os.name");
  public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);

  private static final String _OS_NAME = OS_NAME.toLowerCase(Locale.ENGLISH);
  public static final boolean isWindows = _OS_NAME.startsWith("windows");
  public static final boolean isMac = _OS_NAME.startsWith("mac");
  public static final boolean isLinux = _OS_NAME.startsWith("linux");
  public static final boolean isFreeBSD = _OS_NAME.startsWith("freebsd");
  public static final boolean isSolaris = _OS_NAME.startsWith("sunos");
  public static final boolean isUnix = !isWindows;
  public static final boolean isXWindow = isUnix && !isMac;

  public static final boolean isFileSystemCaseSensitive =
    isUnix && !isMac || "true".equalsIgnoreCase(System.getProperty("idea.case.sensitive.fs"));

  private static final String ARCH_DATA_MODEL = System.getProperty("sun.arch.data.model");
  /** @deprecated inexact, please use {@code com.intellij.util.system.CpuArch} instead */
  @Deprecated

  public static final boolean is64Bit = !(ARCH_DATA_MODEL == null || ARCH_DATA_MODEL.equals("32"));
}
