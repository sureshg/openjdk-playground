package dev.suresh.json

import App
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

internal class KotlinTest {

  @BeforeEach
  fun setUp() {
    println("Setting up the test...")
  }

  @AfterEach
  fun tearDown() {
    println("Tearing down the test!!")
  }

  @Test
  fun version() {
    assertTrue(KotlinVersion.CURRENT.toString() == App.KOTLIN_VERSION.substringBefore("-"))
  }

  @Test
  fun coroutineTest() = runTest {
    val deferred = async {
      delay(1.minutes)
      async {
        delay(2.minutes)
      }.await()
    }

    deferred.await()
  }

  @Test
  fun dispatcherTest() = runTest {
    // Virtual Thread Dispatcher
    val vtDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    withContext(vtDispatcher) {
      delay(100.milliseconds)
    }

    // val DB = newFixedThreadPoolContext(10, "DB")
    // -Dkotlinx.coroutines.io.parallelism=128
    val DB = Dispatchers.IO.limitedParallelism(5)
    withContext(DB) {
      delay(100.milliseconds)
    }

    advanceUntilIdle()
  }
}
