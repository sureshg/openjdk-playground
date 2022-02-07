package dev.suresh;

import static java.lang.System.out;

import dev.suresh.adt.Records;
import dev.suresh.jte.RenderJte;
import dev.suresh.loom.jetty.JettyServerKt;
import dev.suresh.mvn.MavenResolver;
import dev.suresh.npe.HelpfulNPE;
import dev.suresh.server.MockServer;
import dev.suresh.tools.JdkToolsKt;
import java.lang.invoke.MethodHandles;
import java.security.Security;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

record Person(String name, int age) {}

public class Main {

  private String name = "Java";
  private int age = 25;

  public static int computeScore(Person p) {
    return 1;
  }

  public static List<Person> topN(List<Person> persons, int count) {
    record PersonX(Person p, int score) {}
    var list =
        persons.stream()
            .sorted(Comparator.comparing(Main::computeScore))
            .map(p -> new PersonX(p, computeScore(p)))
            .limit(count)
            .map(PersonX::p)
            .toList();
    return list;
  }

  public static void main(String[] args) throws Exception {
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
    out.println("Record Test: " + new Person("Hello Kotlin", 8));

    // Since these 2 properties are part of the security policy, they are not
    // set by either the -D option or the System.setProperty() API
    var secMgr = System.getSecurityManager();
    out.println("Security Manager: " + secMgr);

    final String dnsCacheTTL = "networkaddress.cache.ttl";
    final String dnsCacheNegTTL = "networkaddress.cache.negative.ttl";
    out.println(dnsCacheTTL + " -> " + Security.getProperty(dnsCacheTTL));
    out.println(dnsCacheNegTTL + " -> " + Security.getProperty(dnsCacheNegTTL));
    Security.setProperty(dnsCacheTTL, "30");
    Security.setProperty(dnsCacheNegTTL, "10");
    // showAllSecurityProperties();

    new MockServer().run();
    new MavenResolver().run();
    new RenderJte().run();
    Records.run();
    JettyServerKt.run(8080);
    JdkToolsKt.run();
    HelpfulNPE.run();
  }

  /** @see <a href="https://www.baeldung.com/java-variable-handles">VarHandles</a> */
  private static void showAllSecurityProperties() throws Exception {
    // Should add this VM args "--add-opens=java.base/java.security=ALL-UNNAMED"
    var lookup = MethodHandles.lookup();
    var varHandle =
        MethodHandles.privateLookupIn(Security.class, lookup)
            .findStaticVarHandle(Security.class, "props", Properties.class);
    Properties sec = (Properties) varHandle.get();
    sec.forEach((k, v) -> out.println(k + " --> " + v));
  }

  @Override
  public boolean equals(Object o) {
    return (this == o)
        || (o instanceof Main x) && this.age == x.age && Objects.equals(this.name, x.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }
}
