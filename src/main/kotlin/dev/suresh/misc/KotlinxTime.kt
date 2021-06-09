package dev.suresh.misc

import kotlinx.datetime.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.time.Month
import java.time.format.*

fun main() {
  physicalTime()
  civilTime()
}

/**
 * Wall clock/calender time.
 *
 * Duration - Temporal amount deals with DateTime
 * Period   - Temporal amount deals with Date
 */
private fun civilTime() {

  val iso8601String = "2020-10-18T06:41:05.123743"

  val wallCalender = LocalDate(2020, Month.OCTOBER, 17)
  println(wallCalender)
  println(LocalDate.parse("2020-10-20"))

  val wallClock = LocalDateTime(2020, Month.OCTOBER, 17, 10, 30, 10, 12345678)
  println(wallClock)
  println(LocalDateTime.parse(iso8601String))

  // Formatter is not yet available for kotlinx.time
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
  val dateTime = java.time.LocalDateTime.parse("2020-10-18 06:41:05.123", formatter)
  println(dateTime)

  val tz = TimeZone.currentSystemDefault()
  val now = Clock.System.now()
  val zonedDateTime = now.toLocalDateTime(tz)
  println("Instant now $now")
  println("ZonedDateTime  $zonedDateTime")

  val local = wallCalender.atTime(14, 51, 10, 234)
  TimeZone.availableZoneIds.map { it.split("/") }.groupBy { it.first() }.forEach { t, u ->
    println(t)
    println("--------")
    u.forEachIndexed { index, list ->
    }
  }

  println(
    local.toInstant(TimeZone.of("Europe/Berlin"))
      .toLocalDateTime(TimeZone.of("Asia/Calcutta"))
  )
}

/**
 * Epoch time.
 */
private fun physicalTime() {
  val timestamp = Clock.System.now()
  timestamp.testPrint()

  Instant.DISTANT_FUTURE.testPrint()
  Instant.DISTANT_PAST.testPrint()

  Instant.parse("2020-10-18T06:41:05.123Z").testPrint()
  Instant.fromEpochMilliseconds(1603003265000).testPrint()
  Instant.fromEpochSeconds(1603003265, 123743000).testPrint()
}

fun Instant.testPrint() {
  println("toString               : ${toString()}")
  println("Epoch Seconds          : $epochSeconds")
  println("Epoch Millis           : ${toEpochMilliseconds()}")
  println("Nanoseconds of second  : $nanosecondsOfSecond")
  println("----------------------------------------------")
}
