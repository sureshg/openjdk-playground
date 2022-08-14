package dev.suresh.lang

import java.math.BigDecimal
import kotlin.math.*

/**
 * 64-bit floating points can only represent numbers which are sums of powers of 2.
 *
 * [Floating Point in 60
 * Seconds](https://www.youtube.com/watch?v=Vs7LRqnZLaI)
 */
fun main() {
  // Double-precision 64-bit IEEE 754 floating point
  val dx = 0.1
  val dy = 0.2

  // Single-precision 32-bit IEEE 754 floating point
  val fx = 0.1f
  val fy = 0.2f

  // 0.1 and 0.2 can't represent precisely. It is hidden from you by the fact that
  // floating point number rounds the result when it prints.

  println("[Double] dx: $dx, dy: $dy") // prints x: 0.1, y: 0.2
  println("[Float]  fx: $fx, fy: $fy \n") // prints x: 0.1, y: 0.2

  // The floating error can accumulate on other operations
  val dz = dx + dy
  val fz = fx + fy

  println("[Double] $dx + $dy = $dz") // prints 0.30000000000000004
  println("[Float]  $fx + $fy = $fz \n") // prints 0.3

  // 0.1 and 0.2 are slightly larger than it appears and 0.3 is smaller.
  println(BigDecimal(0.1)) // prints 0.1000000000000000055511151231257827021181583404541015625
  println(BigDecimal(0.2)) // prints 0.200000000000000011102230246251565404236316680908203125
  println(BigDecimal(0.3)) // prints 0.299999999999999988897769753748434595763683319091796875

  val x = BigDecimal(0.1).add(BigDecimal(0.2))
  println(x) // prints 0.3000000000000000166533453693773481063544750213623046875
  println(x.toDouble()) // prints 30000000000000004
  // Use canonical string representation provided by the Double.toString(double) method
  println(BigDecimal.valueOf(0.3)) // prints 0.3

  // To reduce the accumulated error, is to use set precision.
  println("\nReducing the accumulated error")
  println(round(dz * 1e8) / 1e8) // prints 0.3
}
