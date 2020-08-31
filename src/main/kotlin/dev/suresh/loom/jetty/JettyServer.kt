package dev.suresh.loom.jetty

import jakarta.servlet.http.*
import org.eclipse.jetty.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.util.*
import org.eclipse.jetty.util.component.*
import java.net.http.*
import java.time.*
import java.time.Duration
import java.util.concurrent.*
import kotlin.time.*

fun main() {
    run()
}

fun run(port: Int = 8080) {
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
        pumpRequests(server, 500)
    }
    println("Took ${took.inSeconds} seconds")

    // Finally stop the server.
    println("Shutting down the server!")
    LifeCycle.stop(server)
    // server.join()
}

fun pumpRequests(server: Server, count: Int, deadlineInSec: Long = 60L) {
    require(count > 0)
    println("Sending $count concurrent requests to ${server.uri}")

    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .executor(Executors.newVirtualThreadExecutor())
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    val factory = Thread.builder().virtual().name("VirtualThreadPool-", 1).factory()
    val execSvc = Executors.newThreadExecutor(factory)
    val results = execSvc.withDeadline(Instant.now().plusSeconds(deadlineInSec))
        .use {
            (1..count).map { n ->
                it.submitTask {
                    try {
                        println("---> $n. Sending Request")
                        val res = client.send(
                            HttpRequest.newBuilder().uri(server.uri).build(),
                            HttpResponse.BodyHandlers.ofString()
                        )
                        val thread = Thread.currentThread()
                        println("<--- $n. Response(${thread.name}-${thread.id}-${thread.isVirtual}): ${res.body()}")
                        Result.success(res.body())
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }
        }

// // Client using async style.
//    val futures = (1..count).map { n ->
//        CompletableFuture.supplyAsync(
//            {
//                println("---> $n. Sending Request")
//                val res = client.send(
//                    HttpRequest.newBuilder()
//                        .timeout(10.seconds.toJavaDuration())
//                        .uri(server.uri)
//                        .build(),
//                    HttpResponse.BodyHandlers.ofString()
//                )
//                val thread = Thread.currentThread()
//                println("<--- $n. Response(${thread.name}-${thread.id}-${thread.isVirtual}): ${res.body()}")
//                Result.success(res.body())
//            },
//            execSvc
//        ).exceptionally {
//            Result.failure(it)
//        }
//    }
//
//    // Wait for all tasks to complete and prints the response.
//    val results = CompletableFuture.allOf(*futures.toTypedArray())
//        .handle { _, _ -> // or thenRun
//            futures.map { it.join() }
//        }.join()

    val (ok, err) = results.map { it.join() }.partition { it.isSuccess }
    ok.forEachIndexed { i, r ->
        println("$i -> ${r.getOrNull()}")
    }

    println("==== ERRORS ====")
    err.forEachIndexed { i, r ->
        println("err $i -> ${r.exceptionOrNull()?.message}")
    }

    println(
        """
        SUCCESS: ${ok.size} / ${results.size} 
        FAILURE: ${err.size} / ${results.size}
        """.trimIndent()
    )
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
