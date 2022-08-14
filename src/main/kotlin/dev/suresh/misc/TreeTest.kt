package dev.suresh.misc

fun main2() {
  val root =
    generateSequence(Node1(1)) { prev ->
        // Repeatedly generate parent nodes that link to the previous node as their left children
        Node1(prev.data + 1, prev, null)
      }
      .take(100_000)
      .last()
  println(root.depth)
}

data class Node1<T>(val data: T, val left: Node1<T>? = null, val right: Node1<T>? = null)

val <T> Node1<T>?.depth: Int
  get() =
    when (this) {
      null -> 0
      else -> maxOf(left.depth, right.depth) + 1
    }
