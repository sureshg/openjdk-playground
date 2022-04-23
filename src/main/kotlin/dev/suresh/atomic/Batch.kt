package dev.suresh.atomic

import java.util.concurrent.atomic.*

/**
 * If you're using AtomicInteger and friends you can probably save
 * some memory and improve locality by using Atomic*FieldUpdaters.
 *
 * $ ./gradlew run -PappMainClass=dev.suresh.atomic.BatchKt
 */
data class Counter(@Volatile private var count: Int = 0) {

    companion object {
        private val COUNT = AtomicIntegerFieldUpdater.newUpdater(Counter::class.java, "count")
    }

    fun increment(delta: Int) = COUNT.addAndGet(this, delta)
}

fun main() {
    println(Counter().apply { increment(100) })
}
