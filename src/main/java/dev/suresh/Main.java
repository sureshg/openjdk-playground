package dev.suresh;

import dev.suresh.adt.Records;
import dev.suresh.jte.RenderJte;
import dev.suresh.loom.jetty.JettyServerKt;
import dev.suresh.mvn.MavenResolver;
import dev.suresh.server.DevServer;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
    System.out.println(textBlock);
    System.out.println(textBlock.translateEscapes());
    System.out.println("Record Test: " + new Person("Hello Kotlin", 8));

    new DevServer().run();
    new MavenResolver().run();
    new RenderJte().run();
    Records.run();
    JettyServerKt.run(8080);
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
