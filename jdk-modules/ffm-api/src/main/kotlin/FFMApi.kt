package dev.suresh.ffm

import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout
import kotlin.jvm.optionals.getOrNull
import org.unix.Linux
import org.unix.ttysize
import org.unix.winsize

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
    stdLibC()
    // terminal()
  }

  private fun downCalls() {
    currTime()
    strlen("Hello Panama!")
    getPid()
  }

  private fun terminal() {
    if (System.getProperty("os.name").contains("win", ignoreCase = true).not()) {
      Arena.openConfined().use { arena ->
        val winAddr = winsize.allocate(arena)
        val ttyAddr = ttysize.allocate(arena)
        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), winAddr.address())
        Linux.ioctl(Linux.STDIN_FILENO(), Linux.TIOCGWINSZ(), ttyAddr.address())
        println(
            """WinSize {
               | ws_col: ${winsize.`ws_col$get`(winAddr)},
               | ws_row: ${winsize.`ws_row$get`(winAddr)}
               |}"""
                .trimMargin(),
        )
        println(
            """TtySize {
               | ts_lines: ${ttysize.`ts_lines$get`(ttyAddr)},
               | ts_cols: ${ttysize.`ts_cols$get`(ttyAddr)}
               |}"""
                .trimMargin(),
        )
      }
    }
  }

  private fun strlen(str: String) {
    Arena.openConfined().use { offHeap ->
      // Function pointer with zero size.
      val strlenAddr = SYMBOL_LOOKUP.findOrNull("strlen")
      val strlenDescriptor = FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG)
      val strlen = LINKER.downcallHandle(strlenAddr, strlenDescriptor)

      val cString = offHeap.allocateUtf8String(str)
      val strlenResult = strlen.invokeExact(cString.address()) as Long
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

    // Allocate an off-heap region of memory big enough to hold 10 values of the primitive type int,
    // and fill it with values ranging from 0 to 9
    Arena.openConfined().use { arena ->
      val count = 10
      val segment = arena.allocate(count * ValueLayout.JAVA_INT.byteSize())
      for (i in 0..count - 1) {
        segment.setAtIndex(ValueLayout.JAVA_INT, i.toLong(), i)
      }
    }
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
            "wctomb",
        )
        .forEachIndexed { idx, func ->
          val addr = SYMBOL_LOOKUP.findOrNull(func)
          println("${idx + 1}) $func address = ${addr?.address()}")
        }
  }
}

fun main() {
  FFMApi.run()
}
