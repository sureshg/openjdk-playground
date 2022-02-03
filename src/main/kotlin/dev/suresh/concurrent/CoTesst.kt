package dev.suresh.concurrent

import kotlinx.coroutines.*
import kotlin.coroutines.*

object CoError : Throwable("error")

suspend fun main() {

  val s = suspendCancellableCoroutine<Int> { cont ->
    cont.resume(100) {
    }
    cont.resumeWithException(CoError)
  }
}
