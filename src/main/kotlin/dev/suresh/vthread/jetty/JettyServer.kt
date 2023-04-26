package dev.suresh.vthread.jetty

import io.mikael.urlbuilder.UrlBuilder
import jakarta.servlet.http.*
import java.net.http.*
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.concurrent.Executors
import jdk.incubator.concurrent.ScopedValue
import kotlin.time.*
import org.eclipse.jetty.ee10.servlet.ServletContextHandler
import org.eclipse.jetty.server.*
import org.eclipse.jetty.util.*

fun main() {
  run()
}

fun run(args: Array<String>? = emptyArray()) {
  val httpPort = 8080
  println("Starting the Jetty server on $httpPort...")
  val server = Server(VirtualThreadPool())

  val connector =
      ServerConnector(server).apply {
        port = httpPort
        acceptQueueSize = 64
      }
  server.connectors = arrayOf(connector)

  val context = ServletContextHandler()
  context.addServlet(HelloServlet::class.java, "/")
  server.handler = context
  server.start()
  println("Server started at ${server.uri}")

  val took = measureTime { pumpRequests(server, 50) }
  println("Took ${took.toDouble(DurationUnit.SECONDS)} seconds")

  if (args.orEmpty().any { it.equals("--no-shutdown", true) }) {
    server.join()
  } else {
    println("Shutting down the server!")
    server.stop()
  }
}

fun pumpRequests(server: Server, count: Int, deadlineInSec: Long = 5L) {
  require(count > 0)
  println(
      "Sending $count concurrent requests to ${server.uri} and wait for $deadlineInSec seconds...",
  )

  val client =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .followRedirects(HttpClient.Redirect.NORMAL)
          .connectTimeout(Duration.ofSeconds(5))
          .build()

  val factory = Thread.ofVirtual().name("VirtualThreadPool-", 1).factory()
  val execSvc = Executors.newThreadPerTaskExecutor(factory)
  // val ecs = ExecutorCompletionService<String>(execSvc)

  val results =
      execSvc.use { exec ->
        val user = System.getProperty("user.name", "user")

        println("--> Sending $count concurrent requests")
        (1..count).map { idx ->
          exec.submit<Result<String>> {
            try {

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
    """
          .trimIndent(),
  )
}

class HelloServlet : HttpServlet() {
  private val ID = ScopedValue.newInstance<String>()
  private val USER = ScopedValue.newInstance<String>()
  private val OS: String = System.getProperty("os.name")

  init {
    println("Initializing Jakarta Servlet >>>>> ")
  }

  override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val id = req.getParameter("id")
    val user = req.getParameter("user")
    ScopedValue.where(ID, id).where(USER, user).run {
      resp.apply {
        contentType = "application/json"
        status = HttpServletResponse.SC_OK
        writer?.println(exec(req))
      }
    }
  }

  private fun exec(req: HttpServletRequest): String {
    // Simulate blocking
    Thread.sleep(Duration.ofMillis(500))
    return """
          {
            "Id"     : ${ID.orElse("n/a")},
            "User"   : ${USER.orElse("n/a")},
            "server" : Jetty-${Jetty.VERSION},
            "Java"   : ${JavaVersion.VERSION},
            "OS"     : $OS,
            "target" : ${req.fullURL},
            "Thread" : ${Thread.currentThread()}
          }
         """
        .trimIndent()
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
