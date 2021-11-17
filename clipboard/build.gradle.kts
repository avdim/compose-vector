plugins {
  java
  kotlin("jvm")
}

java {
  if (true) {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(11))
      if (false) {
        vendor.set(JvmVendorSpec.BELLSOFT)
        implementation.set(JvmImplementation.VENDOR_SPECIFIC)
      }
    }
  } else {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
