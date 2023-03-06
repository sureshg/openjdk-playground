package dev.suresh

import java.util.concurrent.Executors
import jdk.incubator.concurrent.StructuredTaskScope

@JvmRecord data class RecordKlass(val name: String, val age: Int)

fun main() {
  Executors.newVirtualThreadPerTaskExecutor().use {}
  val s = StructuredTaskScope.ShutdownOnFailure()
}
