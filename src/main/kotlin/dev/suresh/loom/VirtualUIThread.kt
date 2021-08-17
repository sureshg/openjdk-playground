package dev.suresh.loom

import java.awt.*
import java.util.concurrent.*

fun main() {
  val tf = Thread.ofVirtual()
    .name("Virtual EventQueue", 1)
    .scheduler(EventQueue::invokeLater)
    .factory()
  val exec = Executors.newThreadPerTaskExecutor(tf)
  println("Virtual UI thread executor: $exec, factory: $tf")
}
