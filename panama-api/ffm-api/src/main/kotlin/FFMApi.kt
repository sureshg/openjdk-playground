package dev.suresh.ffm

import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySession

object FFMApi {

  @JvmStatic
  fun run() {
    val segment = MemorySegment.allocateNative(10, MemorySession.global())
    println(segment)
    println(System.getProperty("java.version"))
  }
}

fun main() {
  FFMApi.run()
}
