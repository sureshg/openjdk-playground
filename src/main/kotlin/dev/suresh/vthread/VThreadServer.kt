package dev.suresh.vthread

import com.sun.net.httpserver.*
import okhttp3.tls.internal.*
import java.lang.Thread.sleep
import java.net.*
import java.net.http.*
import java.net.http.HttpResponse.BodyHandlers
import java.time.*
import java.util.concurrent.*
import java.util.stream.Collectors.joining
import kotlin.system.*

object VThreadServer {

  private val execSvc = Executors.newVirtualThreadPerTaskExecutor()

  @JvmStatic
  fun run() {
    val took = measureTimeMillis {
      exec()
    }
    println(">> Took ${Duration.ofMillis(took).toSeconds()} seconds!")
  }

  private fun exec() {
    println("Generating a self signed cert...")
    val selfSignedCert = TlsUtil.localhost()
    val cn = selfSignedCert.trustManager.acceptedIssuers[0].subjectX500Principal
    println("Self signed cert: $cn")

    // Starts HTTPS server
    val httpsServer = HttpsServer.create(InetSocketAddress(8443), 1_000)
      .apply {
        httpsConfigurator = HttpsConfigurator(selfSignedCert.sslContext())
        executor = execSvc
        createContext("/", ::root)
        createContext("/top", ::top)
        start()
      }

    val url = "https://localhost:${httpsServer.address.port}"
    println("Started the server on $url")

    val client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(5))
      .sslContext(selfSignedCert.sslContext())
      .version(HttpClient.Version.HTTP_2)
      .executor(execSvc)
      .build()

    println("Sending 500 concurrent requests to $url")
    val futures = (1..500).map {
      CompletableFuture.supplyAsync(
        {
          val res = client.send(
            HttpRequest.newBuilder()
              .uri(URI.create(url))
              .build(),
            BodyHandlers.ofString()
          )
          val thread = Thread.currentThread()
          println("<--- Response(${thread.name}-${thread.threadId()}-${thread.isVirtual}): ${res.body()}")
          res.body()
        },
        execSvc
      ).exceptionally(Throwable::message)
    }

    // Wait for all tasks to complete and prints the response.
    CompletableFuture.allOf(*futures.toTypedArray())
      .handle { _, _ -> // or thenRun
        // Finally stop the server.
        println("Shutting down the server!")
        httpsServer.stop(1)
        futures.map { it.join() }
      }.thenAccept {
        it.forEach(::println)
      }.join()
  }

  private fun root(ex: HttpExchange) {
    val thread = Thread.currentThread()
    println("---> Request(${thread.name}-${thread.threadId()}-${thread.isVirtual}): ${ex.requestMethod} - ${ex.requestURI}")
    // Simulate blocking call.
    sleep(Duration.ofMillis(100))
    ex.responseHeaders.add("Content-Type", "application/json")
    val res =
      """
            {
               "threadId" : ${thread.threadId()},
               "version"  : ${System.getProperty("java.vm.version")},
               "virtual"  : ${thread.isVirtual}
            }
      """.trimIndent().toByteArray()

    ex.sendResponseHeaders(200, res.size.toLong())
    ex.responseBody.apply {
      write(res)
      close()
    }
  }

  private fun top(ex: HttpExchange) {
    println("---> Request: ${ex.requestMethod} - ${ex.requestURI}")
    val res = ProcessHandle.allProcesses().map {
      "${it.pid()} ${it.parent().map(ProcessHandle::pid).orElse(0)} ${
      it.info().startInstant()
        .map(Instant::toString).orElse("-")
      } ${
      it.info().commandLine()
        .orElse("-")
      } ${it.info().user().orElse("-")}"
    }.collect(joining("<br>")).toByteArray()

    ex.responseHeaders.add("Content-Type", "text/html; charset=UTF-8")
    ex.sendResponseHeaders(200, res.size.toLong())
    ex.responseBody.apply {
      write(res)
      close()
    }
  }

  /**
   * Disable hostname verification of JDK http client.
   * http://mail.openjdk.java.net/pipermail/net-dev/2018-November/011912.html
   */
  private fun disableHostnameVerification() =
    System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true")
}
