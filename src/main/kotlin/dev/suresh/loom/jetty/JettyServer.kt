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

fun pumpRequests(server: Server, count: Int, deadlineInSec: Long = 10L) {
    require(count > 0)
    println("Sending $count concurrent requests to ${server.uri} and wait for $deadlineInSec seconds...")

    val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .version(HttpClient.Version.HTTP_1_1)
        .build()

    val factory = Thread.builder().virtual().name("VirtualThreadPool-", 1).factory()
    val execSvc = Executors.newThreadExecutor(factory)
    val results = execSvc.withDeadline(Instant.now().plusSeconds(deadlineInSec))
        .use { exec ->
            (1..count).map {
                exec.submitTask {
                    try {
                        println("---> $it. Sending Request")
                        val res = client.send(
                            HttpRequest.newBuilder().uri(server.uri).build(),
                            HttpResponse.BodyHandlers.ofString()
                        )

                        println("<--- $it. Response($threadInfo): ${res.body()}")
                        Result.success(res.body())
                    } catch (t: Throwable) {
                        Result.failure(t)
                    }
                }
            }
        }

    // Clear the interrupt status
    println("Checking if the current thread has been interrupted: ${Thread.interrupted()}")

    val (ok, err) = results.map { it.join() }.partition { it.isSuccess }
    ok.forEachIndexed { i, r ->
        println("${i + 1} -> ${r.getOrNull()}")
    }

    println("==== ERRORS ====")
    err.forEachIndexed { i, r ->
        val msg = when (val ex = r.exceptionOrNull()) {
            is InterruptedException -> "Task interrupted/cancelled due to timeout!"
            else -> ex?.cause?.message
        }
        println("ERROR ${i + 1} -> $msg")
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
        response: HttpServletResponse?,
    ) {
        Thread.sleep(2 * 1000)
        response?.apply {
            characterEncoding = "utf-8"
            contentType = "application/json"
            outputStream.println(
                """
                {
                  "server" : "Loom-${Jetty.VERSION}",
                  "Java"   : ${JavaVersion.VERSION},
                  "Thread" : ${Thread.currentThread()}
                  "target" : $target,
                 }
                """.trimIndent()
            )
            baseRequest?.isHandled = true
        }
    }
}

val threadInfo get() = Thread.currentThread().run { "$name-$id-$isVirtual" }
