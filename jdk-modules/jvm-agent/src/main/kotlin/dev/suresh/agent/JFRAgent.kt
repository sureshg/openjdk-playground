package dev.suresh.agent

import java.lang.System.Logger.Level.*
import java.lang.instrument.Instrumentation
import java.time.Duration
import jdk.jfr.Configuration
import jdk.jfr.consumer.RecordingStream

val logger: System.Logger = System.getLogger("JFRAgent")

fun premain(agentArgs: String?, inst: Instrumentation?) {
  logger.log(INFO) { "PreMain [StaticLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

fun agentmain(agentArgs: String?, inst: Instrumentation?) {
  logger.log(INFO) { "AgentMain [DynamicLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

fun main() {
  logger.log(INFO) { "JFR Agent Sample..Enter to exit!" }
  readln()
}

private fun readJFRStream() {
  try {
    Thread.startVirtualThread {
      val config = Configuration.getConfiguration("profile")
      RecordingStream(config).use {
        it.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(5))
        it.enable("jdk.JavaMonitorEnter").withThreshold(Duration.ofMillis(10))
        it.enable("jdk.GarbageCollection")
        it.enable("jdk.JVMInformation")

        it.onEvent("jdk.CPULoad") { event ->
          val cpuLoad = event.getDouble("machineTotal")
          logger.log(INFO) { "CPU Load: $cpuLoad" }
        }
        it.onEvent("jdk.JVMInformation") { event ->
          val jvmName = event.getString("jvmName")
          val jvmVersion = event.getString("jvmVersion")
          logger.log(INFO) { "JVM: $jvmName, Version: $jvmVersion" }
        }
        it.onEvent("jdk.JavaMonitorEnter") { event ->
          logger.log(INFO) { "Long held Monitor: $event" }
        }
        it.start()
      }
    }
  } catch (ex: Exception) {
    logger.log(ERROR, "Unable to attach the JFR monitor", ex)
  }
}
