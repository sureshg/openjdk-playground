package dev.suresh.adt;

import static java.lang.System.out;

import java.util.concurrent.CompletableFuture;
import org.jspecify.annotations.NullMarked;

/** Data Oriented Programming (DOP) in Java */
public class DOP {

  public static void main(String[] args) throws Exception {

    @NullMarked
    record Person(String name, int age) {}

    var future = new CompletableFuture<>();
    future.complete("hello");
    out.println(future.get());

    var textBlock =
        """
                        This is text block
                        This will join \
                        with the line : %s
                        "quote" = "added"
                        Escape Start  \n \t \r \b \f end
                        Space Escape-\s\s\s\s\s\s\s\s\s\s-end
                        Regex \\S \\d \\D \\w \\W
                        \\d+
                        Escape char: \u0020 \u00A0 \u2000 \u3000 \uFEFF \u200B \u200C \u200D \u2028 \u2029
                        END
                        """
            .formatted(new Person("Foo", 20));
    out.println(textBlock);
  }
}
