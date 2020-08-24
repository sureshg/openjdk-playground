package dev.suresh.loom.jetty

import jakarta.servlet.http.*
import org.eclipse.jetty.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.util.*
import java.net.http.*
import java.time.Duration
import java.util.concurrent.*
import kotlin.time.*

fun main() {
    run(8081)
}

fun run(port: Int) {
    println("Starting the Jetty server on $port...")
    val tp = LoomThreadPool()
    // val tp = QueuedThreadPool(500)
    val server = Server(tp).apply {
        // NetworkTrafficServerConnector(server)
        val connector = ServerConnector(this)
        connector.port = port
        addConnector(connector)
        handler = HandlerList(
            HelloHandler(),
            DefaultHandler(),
        )
    }

    HttpGenerator.setJettyVersion("Loom-${Jetty.VERSION}")
    server.start()
    println("Server started at ${server.uri}")

    val took = measureTime {
        // pumpRequests(server, 100)
    }
    println("Took ${took.inSeconds} seconds")

    // Finally stop the server.
    println("Shutting down the server!")
    server.join()
    // server.stop()
    // server.destroy()
    // LifeCycle.stop(server)
}

fun pumpRequests(server: Server, count: Int) {
    require(count > 0)
    println("Sending $count concurrent requests to ${server.uri}")
    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_2)
        .build()

    val factory = Thread.builder().virtual().name("VirtualThreadPool-", 1).factory()
    val execSvc = Executors.newThreadExecutor(factory)

    // val execSvc = Executors.newFixedThreadPool(count)
    val futures = (1..count).map { n ->
        CompletableFuture.supplyAsync(
            {
                println("---> $n. Sending Request")
                val res = client.send(
                    HttpRequest.newBuilder()
                        .uri(server.uri)
                        .build(),
                    HttpResponse.BodyHandlers.ofString()
                )
                val thread = Thread.currentThread()
                println("<--- $n. Response(${thread.name}-${thread.id}-${thread.isVirtual}): ${res.body()}")
                res.body()
            },
            execSvc
        ).exceptionally(Throwable::message)
    }

    // Wait for all tasks to complete and prints the response.
    CompletableFuture.allOf(*futures.toTypedArray())
        .handle { _, _ -> // or thenRun
            futures.map { it.join() }
        }.thenAccept {
            it.forEachIndexed { i, s ->
                println("<-- $i) $s")
            }
        }.join()
}

class HelloHandler : AbstractHandler() {
    override fun handle(
        target: String?,
        baseRequest: Request?,
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ) {
        Thread.sleep(2 * 1000)
        response?.apply {
            characterEncoding = "utf-8"
            contentType = "application/json"
            outputStream.println(
                """
                {
                  "server" : "Loom-${Jetty.VERSION}",
                  "target" : $target
                 }
                """.trimIndent()
            )
            baseRequest?.isHandled = true
        }
    }
}
