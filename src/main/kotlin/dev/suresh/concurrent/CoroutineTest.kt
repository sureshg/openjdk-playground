package dev.suresh.concurrent

import java.math.BigInteger
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.*

object CoError : Throwable("error")

suspend fun main() {
  val num = suspendCancellableCoroutine { cont ->
    cont.resume(100) {}
    // cont.resumeWithException(CoError)
    cont.invokeOnCancellation {}
  }

  // For suspending lambda, make it inline.
  printFiveTimes { getString() }

  runBlocking {
    val job = launch {
      factorial(200)
      println("Factorial Job reached end!")
    }
    // Give a delay to give the job time to start.
    delay(1.milliseconds)
    job.cancel()
    println("Job cancelled!")
  }
}

// Support cooperative cancellation
suspend fun factorial(n: Int): BigInteger =
    withContext(Dispatchers.Default) {
      var fact = BigInteger.ONE
      for (i in 1..n) {
        ensureActive() // or yield()
        fact *= i.toBigInteger()
      }
      println("factorial($n) = $fact")
      fact
    }

suspend fun getString(): String {
  delay(10.milliseconds)
  return "Hello"
}

inline fun printFiveTimes(gen: () -> String) {
  repeat(2) { println(gen()) }
}
