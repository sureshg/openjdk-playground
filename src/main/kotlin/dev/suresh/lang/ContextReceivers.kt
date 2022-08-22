package dev.suresh.lang

class Logger(val name: String) {
  fun log(message: String) = println("$name: $message")
}

context(Logger)

fun store(msg: String) {
  log("Stored $msg in the file")
}

fun main() {
  val logger = Logger("main")
  with(logger) { store("Hello") }
}
