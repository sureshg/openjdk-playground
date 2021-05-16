package dev.suresh.loom.jetty

import io.mikael.urlbuilder.*
import jakarta.servlet.http.*
import org.eclipse.jetty.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.util.*
import org.eclipse.jetty.util.component.*
import java.net.http.*
import java.time.*
import java.time.Duration
import java.util.concurrent.*
import kotlin.time.*

/**
 * Scoped variable holding the user id (Replacement for [ThreadLocal])
 */
private val ID = ScopeLocal.inheritableForType(String::class.java)

private val USER = ScopeLocal.inheritableForType(String::class.java)

fun main() {
    run()
}

fun run(port: Int = 8080) {
    println("Starting the Jetty server on $port...")
    val tp = LoomThreadPool()
    // val tp = QueuedThreadPool(200)
    val server = Server(tp)

    // NetworkTrafficServerConnector(server)
    val connector = ServerConnector(server).apply {
        this.port = port
        acceptQueueSize = 1024
    }
    server.addConnector(connector)

    val servletHandler = ServletHandler()
    servletHandler.addServletWithMapping(HelloServlet::class.java, "/")
    server.handler = HandlerList(servletHandler, DefaultHandler())

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
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    val factory = Thread.ofVirtual().name("VirtualThreadPool-", 1).factory()
    val execSvc = Executors.newThreadExecutor(factory, Instant.now().plusSeconds(deadlineInSec))
    val user = System.getProperty("user.name", "user")

    val results = execSvc.use { exec ->
        (1..count).map { idx ->
            exec.submit<Result<String>> {
                try {
                    println("---> $idx. Sending Request")
                    val uri = UrlBuilder
                        .fromUri(server.uri)
                        .addParameter("id", idx.toString())
                        .addParameter("user", user)
                        .toUri()
                    val res = client.send(
                        HttpRequest.newBuilder().uri(uri).build(),
                        HttpResponse.BodyHandlers.ofString()
                    )

                    println("<--- $idx. Response($threadInfo): ${res.body()}")
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

    err.forEachIndexed { i, r ->
        if (i == 0) println("=== ERRORS ===")
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

class HelloServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        val id = req?.getParameter("id")
        val user = req?.getParameter("user")
        ScopeLocal
            .where(ID, id)
            .where(USER, user)
            .run {
                resp?.apply {
                    contentType = "application/json"
                    status = HttpServletResponse.SC_OK
                    writer?.println(exec(req, resp))
                }
            }
    }

    private fun exec(req: HttpServletRequest?, resp: HttpServletResponse?): String {
        // Simulate blocking
        Thread.sleep(3 * 1000)
        return """
          {
            "Id"     : ${ID.orElse("n/a")},
            "User"   : ${USER.orElse("n/a")},
            "server" : Jetty-${Jetty.VERSION},
            "Java"   : ${JavaVersion.VERSION},
            "target" : ${req?.fullURL},
            "Thread" : ${Thread.currentThread()}
          }
        """.trimIndent()
    }
}

val HttpServletRequest.fullURL: String
    get() = when (queryString.isNullOrBlank()) {
        true -> requestURL.toString()
        else -> requestURL.append('?').append(queryString).toString()
    }

val threadInfo get() = Thread.currentThread().run { "$name-$id-$isVirtual" }
