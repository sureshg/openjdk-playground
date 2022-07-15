package dev.suresh.service

import kotlin.math.*
import kotlin.random.*

object PrimeGen {

  private val rand = Random(41)

  fun random(upperbound: Int): List<Long> {
    val to = rand.nextInt(upperbound - 2) + 2
    val from = rand.nextInt(upperbound - 1) + 1
    return primeSeq(to.toLong(), from.toLong())
  }

  fun primeSeq(min: Long, max: Long) = (min..max).filter { it.isPrime }
}

val Long.isPrime get() = (2L..sqrt(this.toDouble()).toLong()).all { this % it != 0L }
