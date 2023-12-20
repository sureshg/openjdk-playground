import static java.lang.System.out;

import de.mirkosertic.bytecoder.api.web.*;
import java.util.List;
import java.util.Objects;

public class Main {

  public static void main(String[] args) {
    out.println("Hello JVM Wasm");
    var window = Window.window();
    var document = window.document();
    int[] counter = {0};
    HTMLButton button = document.getElementById("button");
    button.addEventListener(
        "click",
        (EventListener<MouseEvent>)
            event -> {
              window.document().title("Clicked!" + event.type());
              // button.disabled(true);
              var div = document.getElementById("text");
              var persons = List.of(new Person("Foo", 40), new Person("Bar", 30));
              persons.stream()
                  .map(
                      e -> {
                        Element p = document.createElement("p");
                        p.textContent(
                            counter[0]++ + ": " + e.name() + " is " + e.age() + " years old!");
                        return p;
                      })
                  .forEach(div::appendChild);
            });
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
