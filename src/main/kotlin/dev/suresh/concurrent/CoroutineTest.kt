package dev.suresh.concurrent

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds

object CoError : Throwable("error")

suspend fun main() {
    val num = suspendCancellableCoroutine { cont ->
        cont.resume(100) {
        }
        // cont.resumeWithException(CoError)
        cont.invokeOnCancellation { }
    }

    // For suspending lambda, make it inline.
    printFiveTimes {
        getString()
    }
}

suspend fun getString(): String {
    delay(100.milliseconds)
    return "Hello"
}

inline fun printFiveTimes(gen: () -> String) {
    repeat(5) {
        println(gen())
    }
}
