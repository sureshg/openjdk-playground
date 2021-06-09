package dev.suresh.cli

import com.jakewharton.crossword.*

fun main() {

  val canvas = TextCanvas(30, 30)
  (1..10).forEach {
    canvas.write(0, 0, "\u001B[31mX\u001B[0m")
    canvas.write(0, 2, "\u001B[34mO\u001B[0m")
    canvas.write(1, 0, "Hello World $it")
    println(canvas.toString())
    Thread.sleep(100)
    println("""\x1B[${canvas.height}F""")
  }
}
