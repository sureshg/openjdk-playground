import static java.lang.System.out;

import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.Security;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Currency;
import java.util.HexFormat;
import java.util.Locale;
import java.util.stream.Stream;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class JavaApp {

  void main(String[] args) throws Exception {
    var start = System.currentTimeMillis();
    final var lineSep = System.lineSeparator();

    var rt = Runtime.getRuntime();
    double sizeUnit = 1024f * 1024 * 1014;
    out.printf("%n✧✧✧✧✧ Available Processors: %d ✧✧✧✧✧%n", rt.availableProcessors());
    out.printf(
        "%n✧✧✧✧✧ JVM Memory -> Total Allocated : %.2fGB, Free: %.2fGB, Max Configured: %.2fGB,  ✧✧✧✧✧%n",
        rt.totalMemory() / sizeUnit, rt.freeMemory() / sizeUnit, rt.maxMemory() / sizeUnit);

    out.printf("%n✧✧✧✧✧ Processes ✧✧✧✧✧%n");
    var ps = ProcessHandle.allProcesses().sorted(ProcessHandle::compareTo).toList();
    ps.forEach(
        p -> {
          var pInfo = p.pid() + " : " + p.info();
          out.println(pInfo);
        });

    out.printf("%n✧✧✧✧✧ Trust stores ✧✧✧✧✧%n");
    var tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init((KeyStore) null);
    var issuers =
        Arrays.stream(tmf.getTrustManagers())
            .flatMap(
                tm -> {
                  var x509Tm = (X509TrustManager) tm;
                  return Arrays.stream(x509Tm.getAcceptedIssuers());
                })
            .toList();
    issuers.forEach(cert -> out.println(cert.getIssuerX500Principal()));

    out.printf("%n✧✧✧✧✧ Dns Resolution ✧✧✧✧✧%n");
    var dns = Arrays.stream(InetAddress.getAllByName("google.com")).toList();
    dns.forEach(out::println);

    out.printf("%n✧✧✧✧✧ TimeZones ✧✧✧✧✧%n");
    var tz = ZoneId.getAvailableZoneIds();
    tz.forEach(out::println);

    out.printf("%n✧✧✧✧✧ Charsets ✧✧✧✧✧%n");
    var cs = Charset.availableCharsets();
    cs.forEach((name, charSet) -> out.println(name + " : " + charSet));

    out.printf("%n✧✧✧✧✧ System Locales ✧✧✧✧✧%n");
    var locales = Locale.getAvailableLocales();
    for (Locale locale : locales) {
      out.println(locale);
    }

    out.printf("%n✧✧✧✧✧ System Countries ✧✧✧✧✧%n");
    var countries = Locale.getISOCountries();
    for (String country : countries) {
      out.println(country);
    }

    out.printf("%n✧✧✧✧✧ System Currencies ✧✧✧✧✧%n");
    var currencies = Currency.getAvailableCurrencies();
    for (Currency currency : currencies) {
      out.println(currency);
    }

    out.printf("%n✧✧✧✧✧ System Languages ✧✧✧✧✧%n");
    var languages = Locale.getISOLanguages();
    for (String language : languages) {
      out.println(language);
    }

    out.printf("%n✧✧✧✧✧ Env Variables ✧✧✧✧✧%n");
    var env = System.getenv();
    env.forEach((k, v) -> out.println(k + " : " + v));

    out.printf("%n✧✧✧✧✧ System Properties ✧✧✧✧✧%n");
    var props = System.getProperties();
    props.forEach((k, v) -> out.println(k + " : " + v));

    var fmt = HexFormat.ofDelimiter(", ").withUpperCase().withPrefix("0x");
    out.printf(
        "%n✧✧✧✧✧ I ❤️ Java          = %s%n",
        fmt.formatHex("I ❤️ Java".getBytes(StandardCharsets.UTF_8)));
    out.printf("✧✧✧✧✧ LineSeparator      = %s%n", fmt.formatHex(lineSep.getBytes()));
    out.printf("✧✧✧✧✧ File PathSeparator = %s%n%n", fmt.formatHex(File.pathSeparator.getBytes()));

    out.printf("%n✧✧✧✧✧ Streams ✧✧✧✧✧%n");
    Stream.of("java", "kotlin", "scala", " ")
        .map(String::toUpperCase)
        .filter(s -> !s.isBlank())
        .mapMulti(
            (s, consumer) -> {
              consumer.accept(s);
              consumer.accept(s.toLowerCase());
            })
        .forEach(out::println);

    out.printf("%n✧✧✧✧✧ Additional info in exception ✧✧✧✧✧%n");
    Security.setProperty("jdk.includeInExceptions", "hostInfo,jar");
    try (var s = new Socket()) {
      s.setSoTimeout(1_00);
      s.connect(new InetSocketAddress("localhost", 12345), 1_00);
    } catch (Exception e) {
      out.println(e.getMessage());
      assert e.getMessage().contains("localhost/127.0.0.1:12345");
    }

    var currTime = System.currentTimeMillis();
    var vmTime =
        ProcessHandle.current().info().startInstant().orElseGet(Instant::now).toEpochMilli();
    var stats =
        """

        +---------Summary----------+
        | Processes      : %-5d   |
        | Dns Addresses  : %-5d   |
        | Trust Stores   : %-5d   |
        | TimeZones      : %-5d   |
        | CharSets       : %-5d   |
        | Locales        : %-5d   |
        | Countries      : %-5d   |
        | Languages      : %-5d   |
        | Currencies     : %-5d   |
        | Env Vars       : %-5d   |
        | Sys Props      : %-5d   |
        | Total time     : %-5dms |
        | JVM Startup    : %-5dms |
        | Process Time   : %-5dms |
        +--------------------------+
        """
            .formatted(
                ps.size(),
                dns.size(),
                issuers.size(),
                tz.size(),
                cs.size(),
                locales.length,
                countries.length,
                languages.length,
                currencies.size(),
                env.size(),
                props.size(),
                (currTime - vmTime),
                (start - vmTime),
                (currTime - start));
    out.println(stats);
  }

  /** Starts an HTTP server */
  private void webServer() throws IOException {
    var start = System.currentTimeMillis();
    var server = HttpServer.create(new InetSocketAddress(80), 0);
    server.createContext(
        "/",
        t -> {
          out.println(STR."GET: \{t.getRequestURI()}");
          var res =
              "Java %s running on %s %s"
                  .formatted(
                      System.getProperty("java.version"),
                      System.getProperty("os.name"),
                      System.getProperty("os.arch"));
          t.sendResponseHeaders(200, res.length());
          try (var os = t.getResponseBody()) {
            os.write(res.getBytes());
          }
        });

    server.createContext("/shutdown", t -> server.stop(0));
    server.start();

    var currTime = System.currentTimeMillis();
    // The timestamp returned by the call to getRuntimeMXBean().getStartTime()
    // returns the value *after* basic JVM initialization.
    var vmTime = ManagementFactory.getRuntimeMXBean().getStartTime();
    out.println(STR."Starting Http Server on port \{server.getAddress().getPort()}...");
    out.printf(
        "Started in %d millis! (JVM: %dms, Server: %dms)%n",
        (currTime - vmTime), (start - vmTime), (currTime - start));
  }
}
