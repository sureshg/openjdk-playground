package dev.suresh;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

// Change this to kotlin
// https://github.com/jamesward/easyracer/blob/main/java-loom/src/main/java/Main.java#L67-L90
// https://twitter.com/pressron/status/1608550618846617602
// Happy eyeball kotlin - https://publicobject.com/2022/03/14/uncertainty-in-tests/
public class TestJava {

  //
  <T> List<T> runAll(List<Callable<T>> tasks, int concurrency, Instant deadline) throws Throwable {
    var vtf = Thread.ofVirtual().factory();
    var stf = semaphoreThreadFactory(new Semaphore(concurrency), vtf);
    try (var scope = new StructuredTaskScope.ShutdownOnFailure("http-requests", stf)) {
      var futures = tasks.stream().map(scope::fork).toList();
      scope.joinUntil(deadline);
      scope.throwIfFailed(e -> e); // Propagate exception
      return futures.stream().map(StructuredTaskScope.Subtask::get).toList();
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
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      }
    };
  }
}
