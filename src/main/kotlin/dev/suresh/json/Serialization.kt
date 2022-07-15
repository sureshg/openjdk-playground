package dev.suresh.json

import kotlinx.serialization.json.*

val json = Json {
  explicitNulls = true
  encodeDefaults = true
}

fun main() {
}
