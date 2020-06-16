package dev.suresh.loom;

import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import okhttp3.tls.internal.TlsUtil;

/** An HTTPS server with virtual thread executor for all request handling. */
public class WebServer {

  public static void main(String[] args) throws Exception {

    out.println("Using the virtual unbounded executor!!");
    var tf = Thread.builder().virtual().name("web-worker", 1).factory();
    var execSvc = Executors.newUnboundedExecutor(tf);

    out.println("Generating a self signed cert...");
    var selfSignedCert = TlsUtil.localhost();
    var cn = selfSignedCert.trustManager().getAcceptedIssuers()[0].getSubjectDN();
    out.printf("Self signed cert %s%n", cn);

    // Starts HTTPS server
    var sockAddr = new InetSocketAddress(8443);
    var httpsServer = HttpsServer.create(sockAddr, 1_000);
    var httpsConfig = new HttpsConfigurator(selfSignedCert.sslContext());
    httpsServer.setHttpsConfigurator(httpsConfig);
    httpsServer.createContext("/", WebServer::rootHandle);
    httpsServer.createContext("/top", WebServer::topHandle);
    httpsServer.setExecutor(execSvc);
    httpsServer.start();

    var url = "https://localhost:" + sockAddr.getPort();
    out.printf("Started the server on %s%n", url);

    var client =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2 * 1000))
            .sslContext(selfSignedCert.sslContext())
            .version(Version.HTTP_2)
            .executor(execSvc)
            .build();

    out.printf("Sending 500 concurrent requests to %s...%n", url);
    var futures =
        IntStream.range(1, 500)
            .mapToObj(
                (i) ->
                    CompletableFuture.supplyAsync(
                            () -> {
                              try {
                                HttpResponse<String> res =
                                    client.send(
                                        HttpRequest.newBuilder().uri(URI.create(url)).build(),
                                        BodyHandlers.ofString());
                                var thread = Thread.currentThread();
                                out.printf(
                                    "<-- Response (%s-%s): %s%n",
                                    thread.getName(),
                                    thread.isVirtual() ? "Virtual" : "Regular",
                                    res.body());
                                return res.body();
                              } catch (Exception e) {
                                throw new RuntimeException(e);
                              }
                            },
                            execSvc)
                        .exceptionally(Throwable::getMessage))
            .collect(Collectors.toList());

    // Wait for all tasks to complete and prints the response.
    CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
        .handle(
            (v, t) -> { // or thenRun
              // Finally stop the server.
              httpsServer.stop(1);
              return futures.stream().map(CompletableFuture::join).collect(toList());
            })
        .thenAccept(
            (r) -> {
              for (int i = 0; i < r.size(); i++) {
                out.printf("%d : %s%n", i + 1, r.get(i));
              }
            });
  }

  /**
   * Disable hostname verification of JDK http client.
   *
   * <p>http://mail.openjdk.java.net/pipermail/net-dev/2018-November/011912.html
   */
  private static void disableHostnameVerification() {
    System.setProperty(
        "jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
  }

  /** Http root handler. */
  private static void rootHandle(HttpExchange exchange) throws IOException {
    var thread = Thread.currentThread();
    out.printf(
        "--> Request (%s-%s): %s - %s%n",
        thread.getName(),
        thread.isVirtual() ? "Virtual" : "Regular",
        exchange.getRequestMethod(),
        exchange.getRequestURI());
    var resHeaders = exchange.getResponseHeaders();
    resHeaders.add("Content-Type", "application/json");

    var response =
        """
        { "id" : %s, "version" : "%s","type" : "%s" }
        """
            .formatted(
                thread.getId(),
                System.getProperty("java.vm.version"),
                thread.isVirtual() ? "Virtual" : "Regular")
            .strip()
            .getBytes(UTF_8);
    exchange.sendResponseHeaders(200, response.length);
    var resBody = exchange.getResponseBody();

    // Simulate blocking call.
    try {
      Thread.sleep(Duration.ofMillis(200));
    } catch (InterruptedException ignore) {
    }

    resBody.write(response);
    resBody.close();
  }

  /** Http /top path handler. */
  private static void topHandle(HttpExchange exchange) throws IOException {
    var top =
        ProcessHandle.allProcesses()
            .map(
                ps ->
                    String.format(
                        "%8d %8d %10s %26s %-40s",
                        ps.pid(),
                        ps.parent().map(ProcessHandle::pid).orElse(0L),
                        ps.info().startInstant().map(Object::toString).orElse("-"),
                        ps.info().commandLine().orElse("-"),
                        ps.info().user().orElse("-")))
            .collect(joining("<br>"))
            .getBytes(UTF_8);

    out.printf("Got %s - %s%n", exchange.getRequestMethod(), exchange.getRequestURI());
    var resHeaders = exchange.getResponseHeaders();
    resHeaders.add("Content-Type", "text/html; charset=UTF-8");

    exchange.sendResponseHeaders(200, top.length);
    var resBody = exchange.getResponseBody();
    resBody.write(top);
    resBody.close();
  }
}
