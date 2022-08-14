package dev.suresh.vthread.jetty

import io.mikael.urlbuilder.*
import jakarta.servlet.http.*
import java.net.http.*
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.concurrent.*
import kotlin.time.*
import org.eclipse.jetty.http.*
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.*
import org.eclipse.jetty.servlet.*
import org.eclipse.jetty.util.*
import org.eclipse.jetty.util.component.*

fun main() {
  run()
}

fun run(args: Array<String>? = emptyArray()) {
  val port = 8080
  println("Starting the Jetty server on $port...")
  val tp = VThreadThreadPool()
  // val tp = QueuedThreadPool(200)
  val server = Server(tp)

  // NetworkTrafficServerConnector(server)
  val connector =
    ServerConnector(server).apply {
      this.port = port
      acceptQueueSize = 128
    }
  server.addConnector(connector)

  val servletHandler = ServletHandler()
  servletHandler.addServletWithMapping(HelloServlet::class.java, "/")
  server.handler = HandlerList(servletHandler, DefaultHandler())

  HttpGenerator.setJettyVersion("Loom-${Jetty.VERSION}")
  server.start()
  println("Server started at ${server.uri}")

  val took = measureTime { pumpRequests(server, 100) }
  println("Took ${took.toDouble(DurationUnit.SECONDS)} seconds")

  if (args.orEmpty().any { it.equals("--no-shutdown", true) }) {
    server.join()
  } else {
    println("Shutting down the server!")
    LifeCycle.stop(server)
  }
}

fun pumpRequests(server: Server, count: Int, deadlineInSec: Long = 10L) {
  require(count > 0)
  println(
    "Sending $count concurrent requests to ${server.uri} and wait for $deadlineInSec seconds..."
  )

  val client =
    HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_1_1)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(10))
      .build()

  val factory = Thread.ofVirtual().name("VirtualThreadPool-", 1).factory()
  val execSvc = Executors.newThreadPerTaskExecutor(factory)

  // val ecs = ExecutorCompletionService<String>(execSvc)

  val results =
    execSvc.use { exec ->
      val user = System.getProperty("user.name", "user")

      (1..count).map { idx ->
        exec.submit<Result<String>> {
          try {
            println("---> $idx. Sending Request")
            val uri =
              UrlBuilder.fromUri(server.uri)
                .addParameter("id", idx.toString())
                .addParameter("user", user)
                .toUri()

            val req =
              HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .GET()
                .build()
            val res = client.send(req, BodyHandlers.ofString())

            println("<--- $idx. Response($threadInfo): ${res.statusCode()} - ${res.body()}")
            Result.success(res.body())
          } catch (t: Throwable) {
            Result.failure(t)
          }
        }
      }
    }

  // Clear the interrupt status
  println("Checking if the current thread has been interrupted: ${Thread.interrupted()}")
  val (ok, err) = results.map { it.get() }.partition { it.isSuccess }
  ok.forEachIndexed { i, r -> println("${i + 1} -> ${r.getOrNull()}") }

  err.forEachIndexed { i, r ->
    if (i == 0) println("=== ERRORS ===")
    val msg =
      when (val ex = r.exceptionOrNull()) {
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
  /** Scoped local variable holding the user info. */
  //    private val ID = ExtentLocal.newInstance<String>()
  //
  //    private val USER = ExtentLocal.newInstance<String>()

  private val OS: String = System.getProperty("os.name")

  init {
    println("Initializing the Servlet >>>>> ")
  }

  override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
    val id = req?.getParameter("id")
    val user = req?.getParameter("user")

    //        ExtentLocal
    //            .where(ID, id)
    //            .where(USER, user)
    //            .run {
    //                resp?.apply {
    //                    contentType = "application/json"
    //                    status = HttpServletResponse.SC_OK
    //                    writer?.println(exec(req))
    //                }
    //            }
  }

  private fun exec(req: HttpServletRequest?): String {
    // Simulate blocking
    Thread.sleep(Duration.ofSeconds(2))
    return """
          {

            "server" : Jetty-${Jetty.VERSION},
            "Java"   : ${JavaVersion.VERSION},
            "OS"     : $OS,
            "target" : ${req?.fullURL},
            "Thread" : ${Thread.currentThread()}
          }
    """.trimIndent()
    //        "Id"     : ${ID.orElse("n/a")},
    //        "User"   : ${USER.orElse("n/a")},
  }
}

val HttpServletRequest.fullURL: String
  get() =
    when (queryString.isNullOrBlank()) {
      true -> requestURL.toString()
      else -> requestURL.append('?').append(queryString).toString()
    }

val threadInfo
  get() = Thread.currentThread().run { "$name-${threadId()}-$isVirtual" }
