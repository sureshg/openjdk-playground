package dev.suresh.lang

import com.javax0.sourcebuddy.Compiler
import java.io.*
import java.lang.invoke.*
import java.lang.reflect.Modifier
import java.security.*
import java.util.*
import java.util.spi.*

fun main() {
  run()
}

fun run() {
  val modules = ModuleLayer.boot().modules()
  println("\nFound ${modules.size} jdk modules!")

  val jsModule =
      ModuleLayer.boot().findModule("jdk.jshell").orElseGet { error("No JShell module found!") }

  // Using Tool API
  jsModule.classLoader.using {
    val sl = ServiceLoader.load(javax.tools.Tool::class.java)
    val jShell = sl.first { it.name() == "jshell" }
    println(jShell.name())
  }

  // Using new ToolsProvider API
  val jdeps =
      ToolProvider.findFirst("jdeps").orElseGet { error("jdeps tool is missing in the JDK!") }
  val out = StringWriter()
  val pw = PrintWriter(out)
  jdeps.run(pw, pw, "--version")

  val version = out.toString()
  println("jdeps version: $version")

  // compileJava()
  // showAllSecurityProperties()
}

fun reflections() {
  println("String isFinal = ${Modifier.isFinal(String::class.java.modifiers)}")
}

fun compileJava() {
  Compiler.java()
      .from(
          """
      | package dev.suresh;
      | public class TestApp implements Runnable {
      |    @Override
      |    public void run() {
      |      System.out.println("Hello from TestApp!");
      |    }
      | }
      """
              .trimMargin())
      .compile()
      .load()
      .newInstance(Runnable::class.java)
      .run()
}

/** @see [VarHandles](https://www.baeldung.com/java-variable-handles) */
private fun showAllSecurityProperties() {
  // Should add this VM args "--add-opens=java.base/java.security=ALL-UNNAMED"
  val lookup = MethodHandles.lookup()
  val varHandle =
      MethodHandles.privateLookupIn(Security::class.java, lookup)
          .findStaticVarHandle(Security::class.java, "props", Properties::class.java)
  val sec = varHandle.get() as Properties
  sec.forEach { k: Any, v: Any -> println("$k --> $v") }
}
