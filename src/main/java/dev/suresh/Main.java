package dev.suresh;

import dev.suresh.adt.DOP;
import dev.suresh.ffm.FFMApi;
import dev.suresh.jte.RenderJte;
import dev.suresh.lang.JPMSKt;
import dev.suresh.mvn.MavenResolver;
import dev.suresh.npe.HelpfulNPE;
import dev.suresh.server.MockServer;
import dev.suresh.vthread.jetty.JettyServerKt;

import java.security.Security;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.lang.System.out;

record Person(String name, int age) {
}

public class Main {

    private final String name = "Java";
    private final int age = 25;

    public static int computeScore(Person p) {
        return 1;
    }

    public static List<Person> topN(List<Person> persons, int count) {
        record PersonX(Person p, int score) {
        }

        return persons.stream()
                .sorted(Comparator.comparing(Main::computeScore))
                .map(p -> new PersonX(p, computeScore(p)))
                .limit(count)
                .map(PersonX::p)
                .toList();
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

    private static void securityProperties() {
        var secMgr = System.getSecurityManager();
        out.println("Security Manager: " + secMgr);

        final String dnsCacheTTL = "networkaddress.cache.ttl";
        final String dnsCacheNegTTL = "networkaddress.cache.negative.ttl";
        final String disabledAlgorithms = "jdk.tls.disabledAlgorithms";

        out.println(dnsCacheTTL + " -> " + Security.getProperty(dnsCacheTTL));
        out.println(dnsCacheNegTTL + " -> " + Security.getProperty(dnsCacheNegTTL));
        out.println(disabledAlgorithms + " -> " + Security.getProperty(disabledAlgorithms));

        Security.setProperty(dnsCacheTTL, "30");
        Security.setProperty(dnsCacheNegTTL, "10");
        // showAllSecurityProperties();
    }

    @Override
    public boolean equals(Object o) {
        return (this == o) || (o instanceof Main x) && age == x.age && Objects.equals(name, x.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
