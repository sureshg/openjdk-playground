package dev.suresh.lang

import java.util.*

/**
 * https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/Character.html#unicode
 * https://www.unicode.org/reports/tr29/#Grapheme_Cluster_Boundaries
 * https://docs.oracle.com/javase/tutorial/i18n/text/char.html
 */
fun main() {

  // String that contains the US flag and a grapheme for a 4-member-family.
  val text = "🇺🇸👨‍👩‍👧‍👦"

  println(
    """
      String : $text
      String Length : ${text.length}  (Number of Unicode code units, characters as fixed-width 16-bit entities)
      Code Point Size : ${text.codePoints().count()} (Any surrogate pairs encountered in the sequence are combined)
      UTF-8 ByteArray Size : ${text.encodeToByteArray().size}
      """
      .trimIndent()
  )

  val codePoints = text.codePoints().toArray()
  for (i in codePoints.indices) {
    println(HexFormat.of().withPrefix("0x").toHexDigits(codePoints[i]))
    println(String(codePoints, 0, i + 1))
  }

  println("---CodePoint to String---")
  println(codePoints.codePointsToString())
  println(codePoints.codePointsToString(","))
  println("--------------------------")

  // val fmt =

  //  val bit = BreakIterator.getCharacterInstance()
  //  // bb.set
  //  while (bb.next() != BreakIterator.DONE) {
  //    println(Character.toString(bb.current()))
  //  }
  //
  //  println("----")
  //  "🇺🇸👨‍👩‍👧‍👦".codePoints().forEach { String(IntArray(it), 0, 1) }
  //  println("----")
  //
  //  "🇺🇸👨‍👩‍👧‍👦".codePoints().mapToObj(Character::toString).forEach { println(it) }
}

fun IntArray.codePointsToString(): String = buildString {
  for (cp in this@codePointsToString) {
    appendCodePoint(cp)
  }
}

fun IntArray.codePointsToString(separator: String = "") =
  joinToString(separator) { Character.toString(it) }
