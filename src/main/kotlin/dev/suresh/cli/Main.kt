package dev.suresh.cli

import com.jakewharton.crossword.*

fun main() {

    val canvas = TextCanvas(100, 100)
    (1..10).forEach {
        canvas.write(1, 0, "Hello World $it")
        println(canvas)
        Thread.sleep(1)
    }
}
