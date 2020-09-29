package dev.suresh.loom

import java.io.*
import java.nio.charset.*
import java.time.*
import java.util.concurrent.locks.*

/**
 * Run with -Djdk.tracePinnedThreads=short|full
 *
 * https://github.com/openjdk/loom/blob/fibers/test/jdk/java/lang/Thread/virtual/TracePinnedThreads.java
 */
fun main() {
    val lock = Any()
    val out = System.out
    val baos = ByteArrayOutputStream()
    System.setOut(PrintStream(baos))

    try {
        Thread.startVirtualThread {
            synchronized(lock) {
                val nanos: Long = Duration.ofSeconds(1).toNanos()
                LockSupport.parkNanos(nanos)
            }
        }.join()
        System.out.flush()
    } finally {
        System.setOut(out)
    }

    val output = baos.toString(StandardCharsets.UTF_8)
    println(output)
    val expected = "<== monitors:1"
    check(expected in output) { """expected:"$expected"""" }
}