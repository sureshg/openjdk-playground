package dev.suresh;

import dev.suresh.jte.RenderJte;
import dev.suresh.loom.jetty.JettyServerKt;
import dev.suresh.mvn.MavenResolver;
import dev.suresh.server.DevServer;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

record Person(String nname, int age) {}

public class Main {

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

  public static void main(String[] args) throws IOException, InterruptedException {

    var queue = new LinkedList<String>();
    var a = Integer.valueOf(10);

    //    Socket socket = new Socket();
    //
    //    try (var is = socket.getInputStream();
    //        var isr = new InputStreamReader(is);
    //        var br = new BufferedReader(isr)) {
    //      br.readLine();
    //
    //    } catch (IOException e) {
    //    }

    var ms =
        """
        abcdefg
        adbcdeg \t 1234 \\n
        \sxyxxs \\n absd \\
        1234  \n
        679999
        \u2022
        ---------
        """;
    System.out.println(ms);
    System.out.println(ms.translateEscapes());
    System.out.println("Record Test: " + new Person("Hello Kotlin", 8));

    new DevServer().run();
    new MavenResolver().run();
    new RenderJte().run();
    JettyServerKt.run(8080);

    record T(String a) {}

    record T1(String b) implements Runnable {

      @Override
      public void run() {}
    }

    var rec = Rec.of("sdsds");
    var intRec = new Rec<>(1, 1, "");
    var intRec1 = new Rec<>("sdsd", 1, "");
    var intRec2 = new Rec<>(new RecordSuper(), 1, "");

    System.out.println(rec);
    System.out.println(intRec);
    System.out.println(intRec1);
    System.out.println(intRec2);
  }

  private String name;
  private int age;

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

class RecordSuper {}

interface RecInterface {}

record Rec<T>(T name, int age, String addr) implements RecInterface {

  Rec {
    Objects.requireNonNull(name);
  }

  public Rec(T name, int age) {
    this(name, age, "");
  }

  public Rec(T name) {
    this(name, 0);
  }

  public T name() {
    return name;
  }

  private void test() {}

  public static Rec<String> of(String name) {
    return new Rec<>("sdsdsd");
  }
}
