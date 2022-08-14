package dev.suresh.lang

import java.lang.invoke.*
import java.util.*

/**
 * Run with "-XX:+UnlockDiagnosticVMOptions -XX:+ShowHiddenFrames" to see the hidden classes.
 *
 * --add-opens java.base/java.util=ALL-UNNAMED
 * @since JDK15
 */
fun main() {
  println("Reading Foo bytes...")
  val clBytes = Foo::class.java.toBytes() ?: error("Can't load Foo!")

  println(HiddenData::class.java.toBytes()?.decodeToString())
  println("-------------------------")
  val t = HiddenData("kotlin")
  println(t.javaClass.toBytes()?.decodeToString())

  val lookup = MethodHandles.lookup().defineHiddenClass(clBytes, true)
  val run = lookup.findStatic(lookup.lookupClass(), "run", MethodType.methodType(Void.TYPE))
  // run.invokeExact()

  val mh = MethodHandles.privateLookupIn(Formatter::class.java, MethodHandles.lookup())
  println(MethodHandles.lookup())
  println(mh.lookupClass())

  println(mh.lookupModes())
  val m =
    mh.findVirtual(
      mh.lookupClass(),
      "parse",
      MethodType.methodType(java.util.List::class.java, java.lang.String::class.java)
    )

  println(m.invoke(Formatter(), "%s"))
  // Concurrent
}

interface Foo {
  companion object {

    @JvmStatic
    fun run() {
      println("Inside Foo")
      error("Error from $methodName")
    }
  }
}

data class HiddenData(val name: String)
