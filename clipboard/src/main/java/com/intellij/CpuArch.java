// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij;

import org.jetbrains.annotations.Nullable;

public enum CpuArch {
  X86(32), X86_64(64), ARM64(64), OTHER(0), UNKNOWN(0);

  /**
   * Machine word size, in bits.
   */
  public final int width;

  CpuArch(int width) {
    if (width == 0) {
      try {
        width = Integer.parseInt(System.getProperty("sun.arch.data.model", "32"));
      } catch (NumberFormatException ignored) {
      }
    }
    this.width = width;
  }

  /**
   * <p>A CPU architecture this Java VM is executed on.
   * Here, {@link CpuArch#OTHER} is an architecture not yet supported by JetBrains Runtime,
   * and {@link CpuArch#UNKNOWN} means the code was unable to detect an architecture.</p>
   *
   * <p><b>Note</b>: may not correspond to the actual hardware if a JVM is "virtualized" (e.g. WoW64 or Rosetta 2).</p>
   */
  public static final CpuArch CURRENT;

  static {
    String arch = System.getProperty("os.arch");
    if ("x86_64".equals(arch) || "amd64".equals(arch)) {
      CURRENT = X86_64;
    } else if ("i386".equals(arch) || "x86".equals(arch)) {
      CURRENT = X86;
    } else if ("aarch64".equals(arch) || "arm64".equals(arch)) {
      CURRENT = ARM64;
    } else if (arch == null || arch.trim().isEmpty()) {
      CURRENT = UNKNOWN;
    } else {
      CURRENT = OTHER;
    }
  }

  public static boolean isIntel32() {
    return CURRENT == X86;
  }

  public static boolean isIntel64() {
    return CURRENT == X86_64;
  }

  public static boolean isArm64() {
    return CURRENT == ARM64;
  }

  public static boolean is32Bit() {
    return CURRENT.width == 32;
  }

  /**
   * The method tries to detect whether this JVM is executed in a known emulated environment - Rosetta 2, WoW64, etc.
   */
  public static boolean isEmulated() {
    if (ourEmulated == null) {
      if (CURRENT == X86_64) {
        ourEmulated = SystemInfoRt.isMac && isUnderRosetta() || SystemInfoRt.isWindows && !matchesWindowsNativeArch();
      } else if (CURRENT == X86) {
        ourEmulated = SystemInfoRt.isWindows && !matchesWindowsNativeArch();
      } else {
        ourEmulated = Boolean.FALSE;
      }
    }

    return ourEmulated == Boolean.TRUE;
  }

  private static @Nullable
  Boolean ourEmulated;

  //<editor-fold desc="Emulated environment detection">
  // https://developer.apple.com/documentation/apple-silicon/about-the-rosetta-translation-environment
  private static boolean isUnderRosetta() {
//    try {
//      if (JnaLoader.isLoaded()) {
//        IntByReference p = new IntByReference();
//        SystemB.size_t.ByReference size = new SystemB.size_t.ByReference(SystemB.INT_SIZE);
//        if (SystemB.INSTANCE.sysctlbyname("sysctl.proc_translated", p.getPointer(), size, null, SystemB.size_t.ZERO) != -1) {
//          return p.getValue() == 1;
//        }
//      }
//    }
//    catch (Throwable t) {
//      Logger.getInstance(CpuArch.class).error(t);
//    }

    return false;
  }

  // https://docs.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-getnativesysteminfo
  private static boolean matchesWindowsNativeArch() {
//    try {
//      if (JnaLoader.isLoaded()) {
//        WinBase.SYSTEM_INFO systemInfo = new WinBase.SYSTEM_INFO();
//        Kernel32.INSTANCE.GetNativeSystemInfo(systemInfo);
//        int arch = systemInfo.processorArchitecture.dwOemID.getLow().intValue();
//        if (arch == 0) return CURRENT == X86;
//        if (arch == 9) return CURRENT == X86_64;
//        if (arch == 12) return CURRENT == ARM64;
//      }
//    }
//    catch (Throwable t) {
//      Logger.getInstance(CpuArch.class).error(t);
//    }

    return true;
  }
  //</editor-fold>
}
