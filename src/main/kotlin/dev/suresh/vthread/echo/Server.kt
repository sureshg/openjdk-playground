package dev.suresh.vthread.echo

import java.net.*
import java.net.StandardSocketOptions.SO_REUSEADDR
import java.net.StandardSocketOptions.SO_REUSEPORT
import java.time.*
import java.util.concurrent.atomic.*
import kotlinx.datetime.*
import kotlinx.datetime.Clock

object Server {
  val ports = 9001..9010
  private val conns = LongAdder()
  private val msgs = LongAdder()

  fun run() {
    println("Starting echo server on ports range: $ports")
    ports.forEach { port -> Thread.startVirtualThread { serve(port) } }

    while (true) {
      Thread.sleep(Duration.ofSeconds(2))
      val currDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
      println("$currDateTime - Connections: ${conns.sum()}, Messages: ${msgs.sum()}")
    }
  }

  fun serve(port: Int) {
    ServerSocket(port, 1_000, InetAddress.getByName("0.0.0.0")).use { server ->
      server.setOption(SO_REUSEADDR, true)
      server.setOption(SO_REUSEPORT, true)
      while (true) {
        val client = server.accept()
        conns.increment()
        Thread.startVirtualThread { handle(client) }
      }
    }
  }

  fun handle(socket: Socket) {
    try {
      socket.use { sock ->
        val inr = sock.inputStream.bufferedReader()
        val out = sock.outputStream.bufferedWriter()
        inr.forEachLine {
          msgs.increment()
          out.write(it)
          out.newLine()
        }
      }
    } finally {
      conns.decrement()
    }
  }
}

fun main() {
  Server.run()
}
