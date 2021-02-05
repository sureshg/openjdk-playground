package dev.suresh.service

import java.util.spi.ToolProvider

fun main() {

   val jdeps: ToolProvider? =  ToolProvider.findFirst("javac").orElseGet { null }
    println(jdeps?.run(System.out,System.out,"--help"))
    println(javax.tools.ToolProvider.getSystemJavaCompiler())
}