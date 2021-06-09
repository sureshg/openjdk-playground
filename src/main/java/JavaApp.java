import static java.lang.System.out;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Locale;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class JavaApp {

  public static void main(String[] args) throws Exception {

    final var lineSep = System.lineSeparator();

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

    out.printf("%n✧✧✧✧✧ Env Variables ✧✧✧✧✧%n");
    var env = System.getenv();
    env.forEach((k, v) -> out.println(k + " : " + v));

    out.printf("%n✧✧✧✧✧ System Properties ✧✧✧✧✧%n");
    var props = System.getProperties();
    props.forEach((k, v) -> out.println(k + " : " + v));

    var lineSepHex = HexFormat.of().formatHex(lineSep.getBytes(StandardCharsets.UTF_8));
    out.printf("%n✧✧✧✧✧ LineSeparator = 0x%s%n", lineSepHex);
    out.printf("%n✧✧✧✧✧ File PathSeparator = %s%n", File.pathSeparator);

    var stats =
        """
        +------------------------+
        | Processes      : %-5d |
        | Dns Addresses  : %-5d |
        | Trust Stores   : %-5d |
        | TimeZones      : %-5d |
        | CharSets       : %-5d |
        | Locales        : %-5d |
        | Env Vars       : %-5d |
        | Sys Props      : %-5d |
        +------------------------+
        """
            .formatted(
                ps.size(),
                dns.size(),
                issuers.size(),
                tz.size(),
                cs.size(),
                locales.length,
                env.size(),
                props.size());

    out.println(stats);
  }
}
