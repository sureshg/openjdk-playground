package dev.suresh.ffm

import java.lang.foreign.AddressLayout
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

val LINKER: Linker = Linker.nativeLinker()

val SYMBOL_LOOKUP by lazy {
  val stdlib = LINKER.defaultLookup()
  val loaderLookup = SymbolLookup.loaderLookup()
  SymbolLookup { name -> loaderLookup.find(name).or { stdlib.find(name) } }
}

fun SymbolLookup.findOrNull(name: String) = find(name).getOrNull()

object FFMApi {

  @JvmStatic
  fun run() {
    println("----- Project Panama -----")
    memoryAPIs()
    downCalls()
    // terminal()
  }

  private fun downCalls() {
    currTime()
    strlen("Hello Panama!")
    getPid()
    gmtime()
  }

  private fun strlen(str: String) {
    val strlenAddr = SYMBOL_LOOKUP.findOrNull("strlen")
    val strlenDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_INT, AddressLayout.ADDRESS)
    val strlen = LINKER.downcallHandle(strlenAddr, strlenDescriptor)
    Arena.ofConfined().use { arena ->
      val cString = arena.allocateUtf8String(str)
      val strlenResult = strlen.invokeExact(cString) as Int
      println("""strlen("$str") = $strlenResult""")
    }
  }

  private fun currTime() {
    // Print the current time.
    val timeAddr = SYMBOL_LOOKUP.findOrNull("time")
    val timeDesc = FunctionDescriptor.of(ValueLayout.JAVA_LONG)
    val time = LINKER.downcallHandle(timeAddr, timeDesc)
    val timeResult = time.invokeExact() as Long
    println("time() = $timeResult epochSecond")
  }

  private fun gmtime() {
    val gmtAddr = SYMBOL_LOOKUP.findOrNull("gmtime")
    val gmtDesc =
        FunctionDescriptor.of(
            AddressLayout.ADDRESS.withTargetLayout(TM.LAYOUT), ValueLayout.ADDRESS)
    val gmtime = LINKER.downcallHandle(gmtAddr, gmtDesc)

    Arena.ofConfined().use { arena ->
      val time = arena.allocate(ValueLayout.JAVA_LONG.bitSize())
      time.set(ValueLayout.JAVA_LONG, 0, Instant.now().epochSecond)
      val tmSegment = gmtime.invokeExact(time) as MemorySegment
      println("gmtime() = ${TM(tmSegment)}")
    }
  }

  private fun getPid() {
    val getpidAddr = SYMBOL_LOOKUP.findOrNull("getpid")
    val getpidDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT)
    val getpid = LINKER.downcallHandle(getpidAddr, getpidDesc)
    val pid = getpid.invokeExact() as Int
    assert(pid.toLong() == ProcessHandle.current().pid())
    println("getpid() = $pid")
  }

  /**
   * Allocate memory for two doubles and initialize it.
   *
   * ```c
   *  Struct Point2D {
   *    double x;
   *    double y;
   *  } point = { 1.0, 2.0 };
   * ```
   */
  private fun memoryAPIs() {
    Arena.ofConfined().use { arena ->
      val point = arena.allocate(ValueLayout.JAVA_DOUBLE.bitSize() * 2)
      point.set(ValueLayout.JAVA_DOUBLE, 0, 1.0)
      point.set(ValueLayout.JAVA_DOUBLE, 8, 2.0)
      println("Point Struct = $point")
      println(
          """Struct {
          |  x = ${point.get(ValueLayout.JAVA_DOUBLE, 0)} ,
          |  y = ${point.get(ValueLayout.JAVA_DOUBLE, 8)}
          |}
         """
              .trimMargin(),
      )
    }

    val point2D =
        MemoryLayout.structLayout(
                ValueLayout.JAVA_DOUBLE.withName("x"),
                ValueLayout.JAVA_DOUBLE.withName("y"),
            )
            .withName("Point2D")

    // VarHandle accessors for the struct fields
    val x = point2D.varHandle(PathElement.groupElement("x"))
    val y = point2D.varHandle(PathElement.groupElement("y"))

    Arena.ofConfined().use { arena ->
      // val seg = MemorySegment.allocateNative(8,arena.scope())
      val point = arena.allocate(point2D)
      x.set(point, 1.0)
      y.set(point, 2.0)
      println("Point2D segment = $point")
      println(
          """Point2D {
          |  x = ${x.get(point)} ,
          |  y = ${y.get(point)}
          |}"""
              .trimMargin(),
      )
    }

    // Allocate an off-heap region of memory big enough to hold 10 values of the primitive type int,
    // and fill it with values ranging from 0 to 9
    Arena.ofConfined().use { arena ->
      val count = 10
      val segment = arena.allocate(count * ValueLayout.JAVA_INT.byteSize())
      for (i in 0..count - 1) {
        segment.setAtIndex(ValueLayout.JAVA_INT, i.toLong(), i)
      }
    }
  }

  //  private fun terminal() {
  //    if (System.getProperty("os.name").contains("win", ignoreCase = true).not()) {
  //      Arena.ofConfined().use { arena ->
  //        val winAddr = winsize.allocate(arena)
  //        val ttyAddr = ttysize.allocate(arena)
  //        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), winAddr.address())
  //        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), ttyAddr.address())
  //        println(
  //            """WinSize {
  //               | ws_col: ${winsize.`ws_col$get`(winAddr)},
  //               | ws_row: ${winsize.`ws_row$get`(winAddr)}
  //               |}"""
  //                .trimMargin(),
  //        )
  //        println(
  //            """TtySize {
  //               | ts_lines: ${ttysize.`ts_lines$get`(ttyAddr)},
  //               | ts_cols: ${ttysize.`ts_cols$get`(ttyAddr)}
  //               |}"""
  //                .trimMargin(),
  //        )
  //      }
  //    }
  //  }
}

fun main() {
  FFMApi.run()
}
