import java.util.List;
import java.util.Objects;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        out.println("Hello JVM Wasm");
        var persons = List.of(new Person("Foo", 40), new Person("Bar", 30));
        persons.forEach(out::println);
    }
}

record Person(String name, int age) {
    Person {
        Objects.requireNonNull(name);
        if (age <= 0) {
            throw new IllegalArgumentException("Invalid age " + age);
        }
    }
}
