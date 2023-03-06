package dev.suresh;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import jdk.incubator.concurrent.StructuredTaskScope;

// Change this to kotlin
// https://github.com/jamesward/easyracer/blob/main/java-loom/src/main/java/Main.java#L67-L90
// https://twitter.com/pressron/status/1608550618846617602
public class TestJava {

  //
  <T> List<T> runAll(List<Callable<T>> tasks, int concurrency, Instant deadline) throws Throwable {
    var vtf = Thread.ofVirtual().factory();
    var stf = semaphoreThreadFactory(new Semaphore(concurrency), vtf);
    try (var scope = new StructuredTaskScope.ShutdownOnFailure("http-requests", stf)) {
      List<Future<T>> futures = tasks.stream().map(scope::fork).toList();
      scope.joinUntil(deadline);
      scope.throwIfFailed(e -> e); // Propagate exception
      return futures.stream().map(Future::resultNow).toList();
    }
  }

  ThreadFactory semaphoreThreadFactory(Semaphore s, ThreadFactory tf) {
    return r -> {
      try {
        s.acquire();
        return tf.newThread(
            () -> {
              try {
                r.run();
              } finally {
                s.release();
              }
            });
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    };
  }
}
