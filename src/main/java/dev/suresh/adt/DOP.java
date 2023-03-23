package dev.suresh.adt;

import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

/**
 * Data Oriented Programming (DOP) in Java
 */
public class DOP {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        @NullMarked
        record Person(String name, int age) {
        }

        var future = new CompletableFuture<>();
        var textBlock = """
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
                .formatted(new Person("Foo", 40));
        future.complete(textBlock);
        out.println(future.get());

        amberReflections();
        serializeRecord();
    }

    private static void amberReflections() {
        var sealedClazz = Result.class;
        out.println("Result (Interface)  -> " + sealedClazz.isInterface());
        out.println("Result (Sealed Class) -> " + sealedClazz.isSealed());

        for (Class<?> permittedSubclass : sealedClazz.getPermittedSubclasses()) {
            out.println("\nPermitted Subclass : " + permittedSubclass.getName());
            if (permittedSubclass.isRecord()) {
                out.println(permittedSubclass.getSimpleName() + " record components are,");
                for (RecordComponent rc : permittedSubclass.getRecordComponents()) {
                    out.println(rc);
                }
            }
        }
    }


    private static void serializeRecord() throws Exception {
        // Local record
        record Lang(String name, int year) implements Serializable {

            Lang {
                requireNonNull(name);
                if (year <= 0) {
                    throw new IllegalArgumentException("Invalid year " + year);
                }
            }
        }

        var serialFile = Files.createTempFile("record-serial", "data").toFile();
        serialFile.deleteOnExit();

        try (var oos = new ObjectOutputStream(new FileOutputStream(serialFile))) {
            List<Record> recs = List.of(
                    new Lang("Java", 25),
                    new Lang("Kotlin", 10),
                    (Record) Result.success(100)
            );

            for (Record rec : recs) {
                out.println("Serializing record: " + rec);
                oos.writeObject(rec);
            }
            oos.writeObject(null); // EOF
        }

        try (var ois = new ObjectInputStream(new FileInputStream(serialFile))) {
            Object rec;
            while ((rec = ois.readObject()) != null) {
                var result = switch (rec) {
                    case null -> "n/a";
                    case Lang l when l.year >= 20 -> l.toString();
                    case Lang(var name, var year) -> name;
                    case Result<?> r -> "Result value: " + r.getOrNull();
                    default -> "Invalid serialized data. Expected Result, but found " + rec;
                };

                out.println("Deserialized record: " + rec);
                out.println(result);
            }
        }

        results().forEach(r -> {
            var result = switch (r) {
                case null -> "n/a";
                case Result.Success<?> s -> s.toString();
                case Result.Failure<?> f -> f.toString();
            };
            out.println("Result (Sealed Type): " + result);
        });
    }

    static List<Result<?>> results() {
        return Arrays.asList(getResult(5), getResult(25), getResult(-1));
    }

    static Result<Number> getResult(long l) {
        // Unnecessary boxing required for boolean check in switch expression
        return switch (Long.valueOf(l)) {
            case Long s when s > 0 && s < 10 -> Result.success(s);
            case Long s when s > 10 -> Result.failure(new IllegalArgumentException(String.valueOf(s)));
            default -> null;
        };
    }
}
