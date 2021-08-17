package dev.suresh.tools

import java.io.*
import java.util.*

fun main() {
  run()
}

fun run() {
  val jsModule = ModuleLayer
    .boot()
    .findModule("jdk.jshell")
    .orElseGet { error("No JShell module found!") }

  // Using Tool API
  jsModule.classLoader.using {
    val sl = ServiceLoader.load(javax.tools.Tool::class.java)
    val jShell = sl.first { it.name() == "jshell" }
    println(jShell.name())
  }

  // Using new ToolsProvider API
  val jdeps = java.util.spi.ToolProvider.findFirst("jdeps")
    .orElseGet { error("jdeps tool is missing in the JDK!") }
  val out = StringWriter()
  val pw = PrintWriter(out)
  jdeps.run(pw, pw, "--version")

  val version = out.toString()
  println("jdeps version: $version")
}

fun ClassLoader.using(run: () -> Unit) {
  val cl = Thread.currentThread().contextClassLoader
  try {
    Thread.currentThread().contextClassLoader = this
    run()
  } finally {
    Thread.currentThread().contextClassLoader = cl
  }
}
