package com.intellij

import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.NullPointerException

class Log {
  fun debug(msg: String) {
    println("debug: $msg")
  }

  fun warn(e: Throwable) {
    println("warn exception: $e")
  }

  fun info(e: Throwable) {
    println("info exception: $e")
  }

  fun warn(s: String, e: Throwable) {
    println("warn s: $s, exception: $e")
  }

  fun error(s: String) {

  }

  fun debug(e: Throwable) {

  }

}
