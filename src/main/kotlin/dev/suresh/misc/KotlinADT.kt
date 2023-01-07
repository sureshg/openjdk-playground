package dev.suresh.misc

import kotlin.math.*

sealed class Tree<out T>

data object Empty : Tree<Nothing>()

data class Node<T>(val value: T, val right: Tree<T> = Empty, val left: Tree<T> = Empty) : Tree<T>()

fun <T : Number> Tree<T>.sum(): Long =
    when (this) {
      Empty -> 0
      is Node -> value.toLong() + left.sum() + right.sum()
    }

fun <T> Tree<T>.depth(): Int =
    when (this) {
      Empty -> 0
      is Node -> max(left.depth(), right.depth()) + 1
    }

// fun <T> Tree<T>.printTree(depth: Int = 0) {
//    val indent = " ".repeat(depth)
//    when (this) {
//        Empty -> return
//        is Node -> {
//            println("$indent $value")
//            println("$indent ${left.}")
//            value.toLong() + left.sum() + right.sum()
//        }
//    }
// }
