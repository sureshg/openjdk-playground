package dev.suresh.loom

import java.awt.*
import java.util.concurrent.*

fun main() {
    val tf = Thread.builder().virtual(EventQueue::invokeLater).factory()
    val exec = Executors.newThreadExecutor(tf)
    println("Virtual UI thread executor: $exec, factory: $tf")
}
