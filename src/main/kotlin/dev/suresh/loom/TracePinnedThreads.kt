package dev.suresh.loom

import java.io.*
import java.time.*
import java.util.concurrent.locks.*

/**
 * Run with -Djdk.tracePinnedThreads=short|full
 *
 * - [TracePinnedThreads.java](https://github.com/openjdk/loom/blob/fibers/test/jdk/java/lang/Thread/virtual/TracePinnedThreads.java)
 * - [Loom Troubleshooting Guide](https://wiki.openjdk.java.net/display/loom/Troubleshooting)
 */
fun main() {
    System.setProperty("jdk.tracePinnedThreads", "short")
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

    val output = baos.toString() // default charset
    println(output)
    val expected = "<== monitors:1"
    check(expected in output) { """expected:"$expected"""" }
}
