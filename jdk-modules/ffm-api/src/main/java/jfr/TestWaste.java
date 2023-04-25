package jfr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;

import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordingFile;

public class TestWaste {
    static List<Object> list = new LinkedList<>();
    static Random random = new SecureRandom();

    public static void main(String... args) throws Exception {
        Configuration c = Configuration.getConfiguration("profile");
        Path file = Path.of("recording.jfr");
        Path scrubbed = Path.of("scrubbed.jfr");
        try (Recording r = new Recording(c)) {
            // Old objects that are cleared out should not create waste
            r.enable("jdk.OldObjectSample").with("cutoff", "infinity").withStackTrace();
            // No stack trace waste from allocation sample
            r.enable("jdk.ObjectAllocationSample").with("throttle", "1000/s").withoutStackTrace();
            // Unused threads should not create unreasonable amount of waste
            r.disable("jdk.ThreadStart");
            r.disable("jdk.ThreadStop");
            // jdk.GCPhaseParallel can often, but not always, take up a very
            // large part of the recording. Disable to make test more stable
            r.disable("jdk.GCPhaseParallel");
            r.start();
            // Generate data
            for (int i = 0; i < 5_000_000; i++) {
                foo(50);
                if (i % 3_000_000 == 0) {
                    System.gc();
                }
                if (i % 10_000 == 0) {
                    Thread t = new Thread();
                    t.start();
                }
            }
            r.stop();
            r.dump(file);
            final Map<String, Long> histogram = new HashMap<>();
            try (RecordingFile rf = new RecordingFile(file)) {
                rf.write(
                        scrubbed,
                        event -> {
                            String key = event.getEventType().getName();
                            histogram.merge(key, 1L, (x, y) -> x + y);
                            return true;
                        });
            }
            for (var entry : histogram.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            float fileSize = Files.size(file);
            System.out.printf("File size: %.2f MB\n", fileSize / (1024 * 1024));
            float scrubbedSize = Files.size(scrubbed);
            System.out.printf("Scrubbed size: %.2f MB\n", scrubbedSize / (1024 * 1024));
            float waste = 1 - scrubbedSize / fileSize;
            System.out.printf("Waste: %.2f%%\n", 100 * waste);
            if (waste > 0.10) {
                throw new AssertionError("Found more than 10% waste");
            }
        }

        //        StringBuilder builder = new StringBuilder();
        //        int i = 0;
        //        System.out.println("Starting");
        //        while (!Thread.interrupted()) {
        //            builder.append("abcdesdjakfhjhksdahfasdfjkhlasdfhjkhjkasdhjkasdf");
        // builder.delete(0, 4);
        //            i++;
        //            System.out.println(i);
        //        }
    }

    static void foo(int depth) {
        bar(depth - 1);
    }

    static void bar(int depth) {
        if (depth > 1) {
            if (random.nextBoolean()) {
                foo(depth);
            } else {
                bar(depth - 1);
            }
        } else {
            list.add(new String("hello"));
        }
    }
}
