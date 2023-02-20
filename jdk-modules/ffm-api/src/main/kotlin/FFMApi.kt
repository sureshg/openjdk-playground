package dev.suresh.ffm

import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentScope
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import kotlin.jvm.optionals.getOrNull

val symbolLookup by lazy {
  val linker = Linker.nativeLinker()
  val linkerLookup = linker.defaultLookup()
  val clLinkerLookup = SymbolLookup.loaderLookup()
  SymbolLookup { name -> clLinkerLookup.find(name).or { linkerLookup.find(name) } }
}

object FFMApi {

  @JvmStatic
  fun run() {
    println("===== Project Panama =====")
    val segment = MemorySegment.allocateNative(10, SegmentScope.global())
    println("MemorySegment = $segment")
    println("Layout Address = ${ValueLayout.ADDRESS}")

    val printfMemSegment = symbolLookup.find("printf").getOrNull()
    val putsMemSegment = symbolLookup.find("puts").getOrNull()
    println(printfMemSegment)
    println(putsMemSegment)
  }
}

fun main() {
  FFMApi.run()
}
