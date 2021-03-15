package dev.suresh.loom

import java.awt.*
import java.util.concurrent.*

fun main() {
    val tf = Thread.ofVirtual().scheduler(EventQueue::invokeLater).factory()
    val exec = Executors.newThreadExecutor(tf)
    println("Virtual UI thread executor: $exec, factory: $tf")
}
