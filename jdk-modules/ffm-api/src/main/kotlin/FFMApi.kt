package dev.suresh.ffm

import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentScope
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import kotlin.jvm.optionals.getOrNull
import org.unix.Linux
import org.unix.ttysize
import org.unix.winsize

val symbolLookup by lazy {
  val linkerLookup = Linker.nativeLinker().defaultLookup()
  val clLinkerLookup = SymbolLookup.loaderLookup()
  SymbolLookup { name -> clLinkerLookup.find(name).or { linkerLookup.find(name) } }
}

object FFMApi {

  @JvmStatic
  fun run() {
    println("----- Project Panama -----")
    //    memoryAPIs()
    //    downCalls()
    //    stdLibC()
    //    heapAlloc()
    terminal()
  }

  private fun downCalls() {
    currTime()
    strlen("Hello Panama!")
    getPid()
  }

  private fun terminal() {
    // check if running on linux or mac
    if (System.getProperty("os.name").contains("Mac")) {
      Arena.openConfined().use { arena ->
        val winAddr = winsize.allocate(arena)
        val ttyAddr = ttysize.allocate(arena)
        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), winAddr.address())
        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), ttyAddr.address())
        println(winsize.`ws_row$get`(winAddr))
        println("WinSize")
        println(winsize.`ws_col$get`(winAddr))
        println(ttysize.`ts_lines$get`(ttyAddr))
        println(ttysize.`ts_cols$get`(ttyAddr))
      }

      //      val ttySizeAddr = symbolLookup.find("ioctl").getOrNull()
      //      val ttySizeDesc = FunctionDescriptor.of(
      //        ValueLayout.JAVA_INT,
      //        ValueLayout.JAVA_INT,
      //        ValueLayout.JAVA_LONG
      //      )
      //      val ttySize = Linker.nativeLinker().downcallHandle(ttySizeAddr, ttySizeDesc)
      //      val fd = 0 // stdin
      //      val buf = MemorySegment.allocateNative(8, SegmentScope.auto())
      //      buf.setI64(0, 0)
      //      val result = ttySize.invokeExact(fd, 0x40087468, buf.address())
      //      println("ioctl($fd, TIOCGWINSZ, $buf) = $result")
      //      println("ioctl($fd, TIOCGWINSZ, $buf) = ${buf.getI32(0)}")
      //      println("ioctl($fd, TIOCGWINSZ, $buf) = ${buf.getI32(4)}")
    }
  }

  private fun strlen(str: String) {
    Arena.openConfined().use { offHeap ->
      // Function pointer with zero size.
      val strlenAddr = symbolLookup.find("strlen").getOrNull()
      val strlenDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG)
      val strlen = Linker.nativeLinker().downcallHandle(strlenAddr, strlenDescriptor)

      val data = offHeap.allocateUtf8String(str)
      val data1 = MemorySegment.allocateNative(str.length.toLong() + 1, offHeap.scope())
      data1.setUtf8String(0, str)

      val strlenResult = strlen.invokeExact(data.address()) as Long
      println("""strlen("$str") = $strlenResult""")
    }
  }

  private fun currTime() {
    // Print the current time.
    val timeAddr = symbolLookup.find("time").getOrNull()
    val timeDesc = FunctionDescriptor.of(ValueLayout.JAVA_LONG)
    val time = Linker.nativeLinker().downcallHandle(timeAddr, timeDesc)
    val timeResult = time.invokeExact() as Long
    println("time() = $timeResult epochSecond")
  }

  private fun getPid() {
    val getpidAddr = symbolLookup.find("getpid").getOrNull()
    val getpidDesc = FunctionDescriptor.of(ValueLayout.JAVA_INT)
    val getpid = Linker.nativeLinker().downcallHandle(getpidAddr, getpidDesc)
    val pid = getpid.invokeExact() as Int
    assert(pid.toLong() == ProcessHandle.current().pid())
    println("getpid() = $pid")
  }

  private fun heapAlloc() {
    val onHeap = MemorySegment.ofArray("test".encodeToByteArray())
    val offHeap = MemorySegment.allocateNative(8, SegmentScope.auto())
    assert(onHeap.isNative.not())
    assert(offHeap.isNative)
  }

  private fun stdLibC() {
    listOf(
            "abort",
            "abs",
            "acos",
            "asctime",
            "asin",
            "assert",
            "atan",
            "atan2",
            "atexit",
            "atof",
            "atoi",
            "atol",
            "bsearch",
            "calloc",
            "ceil",
            "clearerr",
            "clock",
            "cos",
            "cosh",
            "ctime",
            "difftime",
            "div",
            "exit",
            "exp",
            "fabs",
            "fclose",
            "feof",
            "ferror",
            "fflush",
            "fgetc",
            "fgetpos",
            "fgets",
            "floor",
            "fmod",
            "fopen",
            "fprintf",
            "fputc",
            "fputs",
            "fread",
            "free",
            "freopen",
            "frexp",
            "fscanf",
            "fseek",
            "fsetpos",
            "ftell",
            "fwrite",
            "getc",
            "getchar",
            "getenv",
            "gets",
            "gmtime",
            "isalnum",
            "isalpha",
            "iscntrl",
            "isdigit",
            "isgraph",
            "islower",
            "isprint",
            "ispunct",
            "isspace",
            "isupper",
            "isxdigit",
            "labs",
            "ldexp",
            "log",
            "log10",
            "longjmp",
            "malloc",
            "mblen",
            "mbstowcs",
            "mbtowc",
            "memchr",
            "memcmp",
            "memcpy",
            "memmove",
            "memset",
            "mktime",
            "modf",
            "perror",
            "pow",
            "printf",
            "putc",
            "putchar",
            "puts",
            "qsort",
            "raise",
            "rand",
            "realloc",
            "remove",
            "rename",
            "rewind",
            "scanf",
            "setbuf",
            "setjmp",
            "setlocale",
            "setvbuf",
            "signal",
            "sin",
            "sinh",
            "sprintf",
            "sqrt",
            "srand",
            "sscanf",
            "strcat",
            "strchr",
            "strcmp",
            "strcoll",
            "strcpy",
            "strcspn",
            "strerror",
            "strftime",
            "strlen",
            "strncat",
            "strncmp",
            "strncpy",
            "strpbrk",
            "strrchr",
            "strspn",
            "strstr",
            "strtod",
            "strtok",
            "strtol",
            "strtoul",
            "strxfrm",
            "system",
            "tan",
            "tanh",
            "time",
            "tmpfile",
            "tmpnam",
            "tolower",
            "toupper",
            "ungetc",
            "va_arg",
            "va_end",
            "va_start",
            "vfprintf",
            "vprintf",
            "vsprintf",
            "wcstombs",
            "wctomb")
        .forEachIndexed { idx, func ->
          val addr = symbolLookup.find(func).getOrNull()
          println("${idx + 1}) $func address = ${addr?.address()}")
        }
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
    Arena.openConfined().use { arena ->
      val point = arena.allocate(8 * 2)
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

    Arena.openConfined().use { arena ->
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
  }
}

fun main() {
  FFMApi.run()
}
