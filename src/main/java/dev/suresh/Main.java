package dev.suresh;

import static java.lang.System.out;

import dev.suresh.adt.DOP;
import dev.suresh.ffm.FFMApi;
import dev.suresh.jte.RenderJte;
import dev.suresh.lang.JPMSKt;
import dev.suresh.mvn.MavenResolver;
import dev.suresh.npe.HelpfulNPE;
import dev.suresh.server.MockServer;
import dev.suresh.vthread.jetty.JettyServerKt;
import java.security.Security;
import java.util.*;

record Person(String name, int age) {}

public class Main {

  private String name = "Java";
  private int age = 25;

  public static int computeScore(Person p) {
    return 1;
  }

  public static List<Person> topN(List<Person> persons, int count) {
    record PersonX(Person p, int score) {}

    return persons.stream()
        .sorted(Comparator.comparing(Main::computeScore))
        .map(p -> new PersonX(p, computeScore(p)))
        .limit(count)
        .map(PersonX::p)
        .toList();
  }

  void main(String[] args) throws Exception {
    var textBlock =
        """
                This is a textBlock
                example \t introduced \\n in
                \s Java 15. \\n It \\
                avoids the
                need for most escape sequences.
                \u2022
                """;
    out.println(textBlock);
    out.println(textBlock.translateEscapes());
    out.println(STR."Record Test: \{new Person("Hello Kotlin", 8)}");

    // Lossy conversion in compound assignments. Warning should be thrown if lint is enabled.
    var i = 100;
    i += 0.2;

    securityProperties();
    new MockServer().run();
    new MavenResolver().run();
    new RenderJte().run();
    DOP.run();
    JettyServerKt.run(args);
    JPMSKt.run();
    HelpfulNPE.run();
    FFMApi.run();
  }

  private void securityProperties() {
    var secMgr = System.getSecurityManager();
    out.println(STR."Security Manager (Deprecated): \{secMgr}");

    final String dnsCacheTTL = "networkaddress.cache.ttl";
    final String dnsCacheNegTTL = "networkaddress.cache.negative.ttl";
    final String disabledAlgorithms = "jdk.tls.disabledAlgorithms";

    out.println(STR."\{dnsCacheTTL} -> \{Security.getProperty(dnsCacheTTL)}");
    out.println(STR."\{dnsCacheNegTTL} -> \{Security.getProperty(dnsCacheNegTTL)}");
    out.println(STR."\{disabledAlgorithms} -> \{Security.getProperty(disabledAlgorithms)}");

    Security.setProperty(dnsCacheTTL, "30");
    // Large value for the cache for negative responses is problematic.
    // Caching the negative response means that for that much seconds
    // the application will not be able to connect to the server.
    Security.setProperty(dnsCacheNegTTL, "1");
    // showAllSecurityProperties();

    // The length of time after a record expires that it should be retained in the cache.
    // It means that the overall timeout now is ttl+stale (Since Java 22)
    // networkaddress.cache.stale.ttl=10000
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Main m && age == m.age && Objects.equals(name, m.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }
}
