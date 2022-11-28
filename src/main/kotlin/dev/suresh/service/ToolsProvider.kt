package dev.suresh.service

import java.util.spi.ToolProvider
import kotlin.jvm.optionals.getOrNull

fun main() {
  val jdeps = ToolProvider.findFirst("jdeps").getOrNull()
  println(jdeps?.run(System.out, System.out, "--help"))
  println(javax.tools.ToolProvider.getSystemJavaCompiler())
}
