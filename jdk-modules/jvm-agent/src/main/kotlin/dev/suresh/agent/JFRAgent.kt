package dev.suresh.agent

import java.lang.System.Logger.Level.*
import java.lang.instrument.Instrumentation
import java.time.Duration
import jdk.jfr.consumer.RecordingStream
import kotlin.concurrent.thread

val logger: System.Logger = System.getLogger("JFRAgent")

fun premain(agentArgs: String?, inst: Instrumentation?) {
  logger.log(INFO) { "PreMain [StaticLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

private fun readJFRStream() {
  try {
    thread {
      RecordingStream().use {
        it.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(1))
        it.onEvent("jdk.CPULoad") { event ->
          val cpuLoad = event.getDouble("machineTotal")
          logger.log(INFO) { "CPU Load: $cpuLoad" }
        }
      }
    }
  } catch (ex: Exception) {
    logger.log(ERROR, "Unable to attach the JFR monitor", ex)
  }
}

fun agentmain(agentArgs: String?, inst: Instrumentation?) {
  logger.log(INFO) { "AgentMain [DynamicLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

fun main() {
  logger.log(INFO) { "JFR Agent Sample..Enter to exit!" }
  readln()
}
