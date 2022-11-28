package dev.suresh.ffm

import java.lang.foreign.Linker
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySession
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import kotlin.jvm.optionals.getOrNull

val symbolLookup by lazy {
  val linker = Linker.nativeLinker()
  val linkerLookup = linker.defaultLookup()
  val clLinkerLookup = SymbolLookup.loaderLookup()
  SymbolLookup { name -> clLinkerLookup.lookup(name).or { linkerLookup.lookup(name) } }
}

object FFMApi {

  @JvmStatic
  fun run() {
    println("===== Project Panama =====")
    val segment = MemorySegment.allocateNative(10, MemorySession.global())
    println("MemorySegment = $segment")
    println("Layout Address = ${ValueLayout.ADDRESS}")

    val printfMemSegment = symbolLookup.lookup("printf").getOrNull()
    val putsMemSegment = symbolLookup.lookup("puts").getOrNull()
    println(printfMemSegment)
    println(putsMemSegment)
  }
}

fun main() {
  FFMApi.run()
}
