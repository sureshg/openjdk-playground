package dev.suresh.ffm

import java.lang.foreign.AddressLayout.ADDRESS
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout.JAVA_DOUBLE
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.foreign.ValueLayout.JAVA_SHORT
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
    terminal()
  }

  private fun downCalls() {
    currTime()
    strlen("Hello Panama!")
    getPid()
    gmtime()
  }

  private fun strlen(str: String) {
    val strlenAddr = SYMBOL_LOOKUP.findOrNull("strlen")
    val strlenDescriptor = FunctionDescriptor.of(JAVA_INT, ADDRESS)
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
    val timeDesc = FunctionDescriptor.of(JAVA_LONG)
    val time = LINKER.downcallHandle(timeAddr, timeDesc)
    val timeResult = time.invokeExact() as Long
    println("time() = $timeResult epochSecond")
  }

  private fun gmtime() {
    val gmtAddr = SYMBOL_LOOKUP.findOrNull("gmtime")
    val gmtDesc = FunctionDescriptor.of(ADDRESS.withTargetLayout(TM.LAYOUT), ADDRESS)
    val gmtime = LINKER.downcallHandle(gmtAddr, gmtDesc)

    Arena.ofConfined().use { arena ->
      val time = arena.allocate(JAVA_LONG.bitSize())
      time.set(JAVA_LONG, 0, Instant.now().epochSecond)
      val tmSegment = gmtime.invokeExact(time) as MemorySegment
      println("gmtime() = ${TM(tmSegment)}")
    }
  }

  private fun getPid() {
    val getpidAddr = SYMBOL_LOOKUP.findOrNull("getpid")
    val getpidDesc = FunctionDescriptor.of(JAVA_INT)
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
      val point = arena.allocate(JAVA_DOUBLE.bitSize() * 2)
      point.set(JAVA_DOUBLE, 0, 1.0)
      point.set(JAVA_DOUBLE, 8, 2.0)
      println("Point Struct = $point")
      println(
          """Struct {
          |  x = ${point.get(JAVA_DOUBLE, 0)} ,
          |  y = ${point.get(JAVA_DOUBLE, 8)}
          |}
         """
              .trimMargin(),
      )
    }

    val point2D =
        MemoryLayout.structLayout(
                JAVA_DOUBLE.withName("x"),
                JAVA_DOUBLE.withName("y"),
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
      val segment = arena.allocate(count * JAVA_INT.byteSize())
      for (i in 0..count - 1) {
        segment.setAtIndex(JAVA_INT, i.toLong(), i)
      }
    }
  }

  private fun terminal() {
    if (System.getProperty("os.name").contains("win", ignoreCase = true).not()) {
      val winsize =
          MemoryLayout.structLayout(
                  JAVA_SHORT.withName("ws_row"),
                  JAVA_SHORT.withName("ws_col"),
                  JAVA_SHORT.withName("ws_xpixel"),
                  JAVA_SHORT.withName("ws_ypixel"),
              )
              .withName("winsize")

      val wsRow = winsize.varHandle(PathElement.groupElement("ws_row"))
      val wsCol = winsize.varHandle(PathElement.groupElement("ws_col"))
      val wsXpixel = winsize.varHandle(PathElement.groupElement("ws_xpixel"))
      val wsYpixel = winsize.varHandle(PathElement.groupElement("ws_ypixel"))

      val ioctlFun =
          FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_LONG, ADDRESS.withTargetLayout(winsize))
      val isAttyFun = FunctionDescriptor.of(JAVA_INT, JAVA_INT)

      // For capturing the errno value
      val ccs = Linker.Option.captureCallState("errno")
      val csLayout = Linker.Option.captureStateLayout()
      val errnoHandle = csLayout.varHandle(PathElement.groupElement("errno"))

      val ioctl = downcallHandle("ioctl", ioctlFun, ccs, Linker.Option.firstVariadicArg(2))
      val isAtty = downcallHandle("isatty", isAttyFun)

      Arena.ofConfined().use { arena ->
        val isTerminal = isAtty.invokeExact(1) as Int != 0
        if (isTerminal) {
          val winSeg = arena.allocate(winsize)
          val capturedState = arena.allocate(csLayout)
          val winRet = ioctl.invokeExact(capturedState, 1, 0x40087468L, winSeg) as Int

          if (winRet == -1) {
            val errno = errnoHandle.get(capturedState) as Int
            println("ioctl() errno: $errno")
          } else {
            println(
                """
              winsize {
                ws_row = ${wsRow.get(winSeg)}
                ws_col = ${wsCol.get(winSeg)}
                ws_xpixel = ${wsXpixel.get(winSeg)}
                ws_ypixel = ${wsYpixel.get(winSeg)}
              }
            """
                    .trimIndent())
          }
        } else {
          println("Not a TTY")
        }
      }
    }
  }

  private fun downcallHandle(
      symbol: String,
      fdesc: FunctionDescriptor,
      vararg options: Linker.Option
  ) = SYMBOL_LOOKUP.find(symbol).map { LINKER.downcallHandle(it, fdesc, *options) }.orElseThrow()
}

fun main() {
  FFMApi.run()
}
