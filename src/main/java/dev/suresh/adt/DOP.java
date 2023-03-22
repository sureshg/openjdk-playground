package dev.suresh.adt;

import static java.lang.System.out;
import static java.util.Objects.requireNonNull;

import java.io.*;
import java.lang.invoke.VarHandle;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NullMarked;

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

    static void results(String[] args) {
        printResult(getResult(5));
        printResult(getResult(25));
    }

    static Result<Integer> getResult(int i) {
        return i > 10
                ? Result.success(i)
                : Result.failure(new IllegalArgumentException(String.valueOf(i)));
    }

    static <T> void printResult(Result<T> r) {
        System.out.println("""
                ToString  -> %1$s
                Result    -> %2$s
                Success   -> %3$s
                Failure   -> %4$s
                Exception -> %5$s
                """
                .formatted(
                        r.toString(), r.getOrNull(), r.isSuccess(), r.isFailure(), r.exceptionOrNull()));
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
    }
}
