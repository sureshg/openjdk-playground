package dev.suresh.vthread

import java.io.*
import java.time.*
import java.util.concurrent.locks.*

/**
 * Run with -Djdk.tracePinnedThreads=short|full
 *
 * [TracePinnedThreads.java](https://github.com/openjdk/jdk/blob/master/test/jdk/java/lang/Thread/virtual/TracePinnedThreads.java)
 *
 * [VirtualThreadTests](https://github.com/openjdk/jdk/tree/master/test/jdk/java/lang/Thread/virtual)
 *
 * [LoomTroubleshootingGuide](https://wiki.openjdk.java.net/display/loom/Troubleshooting)
 */
fun main() {
  System.setProperty("jdk.tracePinnedThreads", "full")
  tracePinnedThread()
}

fun tracePinnedThread() {
  val lock = Object()
  val out = System.out
  val baos = ByteArrayOutputStream()
  System.setOut(PrintStream(baos))

  try {
    Thread.ofVirtual()
        .start {
          synchronized(lock) {
            val nanos: Long = Duration.ofSeconds(1).toNanos()
            LockSupport.parkNanos(nanos)
            // OR lock.wait()
          }
        }
        .join()
    System.out.flush()
  } finally {
    System.setOut(out)
  }

  val output = baos.toString() // default charset
  println(output)
  val expected = "<== monitors:1"
  check(expected in output) { """expected:"$expected"""" }
}
