package dev.suresh.service

import dev.suresh.lang.*
import java.util.spi.*

fun main() {
    val jdeps = ToolProvider.findFirst("jdeps").orNull()
    println(jdeps?.run(System.out, System.out, "--help"))
    println(javax.tools.ToolProvider.getSystemJavaCompiler())
}
