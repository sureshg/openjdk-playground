package dev.suresh.vthread.echo

import kotlinx.datetime.*
import kotlinx.datetime.Clock
import java.net.InetSocketAddress
import java.net.Socket
import java.time.*
import java.util.concurrent.atomic.*

object Client {

  const val count = 10
  private val conns = LongAdder()
  private val msgs = LongAdder()
  val error = AtomicReference<Throwable?>(null)

  fun run() {
    println("Connecting to server on ports range: ${Server.ports}")
    Server.ports.forEach { port ->
      (1..count).forEach { id ->
        Thread.startVirtualThread { connect(id, port) }
      }
    }

    while (true) {
      Thread.sleep(Duration.ofSeconds(2))
      val currDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
      println("$currDateTime - Connections: ${conns.sum()}, Messages: ${msgs.sum()}")
    }
  }

  fun connect(id: Int, port: Int) {
    val cid = "client $id-$port"
    try {
      Socket().use { sock ->
        sock.connect(InetSocketAddress("localhost", port), 5_000)
        sock.soTimeout = 10_000
        conns.increment()

        val inr = sock.inputStream.bufferedReader()
        val out = sock.outputStream.bufferedWriter()

        val msg = "Hello from $cid"
        out.write(msg)
        out.newLine()
        println("Wrote: $msg and reading response")
        inr.forEachLine {
          msgs.increment()
          println(it)
        }
      }
    } catch (e: Throwable) {
      println("$cid : ${e.message}")
    }
  }
}

fun main() {
  Client.run()
}
