package dev.suresh.json

import App
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue

internal class KotlinTest {

  @BeforeEach
  fun setUp() {
    println("Setting up the test...")
  }

  @AfterEach
  fun tearDown() {
    println("Tearing down the test!!")
  }

  @Test
  fun version() {
    assertTrue(KotlinVersion.CURRENT.toString() == App.KOTLIN_VERSION.substringBefore("-"))
  }
}
