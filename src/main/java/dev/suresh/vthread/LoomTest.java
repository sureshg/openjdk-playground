package dev.suresh.vthread;

import static java.lang.System.out;

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class LoomTest {

  public static void main(String[] args) throws Exception {
    var tf = Thread.ofVirtual().name("task-", 1).factory();
    try (var execSvc = Executors.newThreadPerTaskExecutor(tf)) {

      var tasks =
          Stream.generate(() -> (Callable<String>) () -> fetch("https://google.com"))
              .limit(20)
              .toList();

      out.println("Start invoke all");
      var start = System.nanoTime();
      var result =
          execSvc.invokeAll(tasks, 10, TimeUnit.SECONDS).stream()
              .map(Future::join)
              .map(String::length)
              .toList();

      out.println(result.size());
      result.forEach(out::println);
      var end = System.nanoTime();
      out.println(">>> " + (end - start) / 1_000_000);

      out.println("Start the streaming....");
      // Stream form of ExecutorCompletionService<>();
      start = System.nanoTime();
      execSvc.submit(tasks).map(Future::join).map(String::length).forEach(out::println);

      end = System.nanoTime();
      out.println(">>> " + (end - start) / 1_000_000);

      //    var t = Thread.ofVirtual().unstarted(Thread::dumpStack);
      //    t.start();
      //    System.out.println("Daemon: " + t.isDaemon());
      //    t.join();
      //
      //    var tf = Thread.ofVirtual().name("tf-", 0).factory();
      //    System.out.println(tf);
      //
      //    var counter = new AtomicInteger();
      //    try (var execSvc = Executors.newVirtualThreadExecutor()) {
      //      IntStream.range(0, 1_000_000)
      //          .forEach(
      //              i -> {
      //                execSvc.submit(counter::incrementAndGet);
      //              });
      //    }
      //    System.out.println("Counter: " + counter);

    }
  }

  public static String fetch(String url) throws IOException {
    try {
      out.println("Fetching (" + Thread.currentThread() + ") : " + url);
      try (var in = URI.create(url).toURL().openStream()) {
        return new String(in.readAllBytes(), StandardCharsets.UTF_8);
      }
    } catch (SocketException ex) {
      out.println(Thread.currentThread() + " : " + ex.getMessage());
      throw ex;
    }
  }
}
