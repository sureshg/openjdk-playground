package dev.suresh.agent

import java.lang.System.Logger.Level.ERROR
import java.lang.System.Logger.Level.INFO
import java.lang.instrument.Instrumentation
import java.time.Duration
import jdk.jfr.Configuration
import jdk.jfr.Event
import jdk.jfr.FlightRecorder
import jdk.jfr.consumer.EventStream
import jdk.jfr.consumer.RecordingStream

val logger: System.Logger = System.getLogger("JFRAgent")

fun premain(agentArgs: String?, inst: Instrumentation?) {
  // Generate a JFR event every 5 seconds
  addPeriodicEvent(AgentType())
  logger.log(INFO) { "PreMain [StaticLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

fun agentmain(agentArgs: String?, inst: Instrumentation?) {
  addPeriodicEvent(AgentType(type = "dynamic"))
  logger.log(INFO) { "AgentMain [DynamicLoad]: Attaching the JFR Monitor..." }
  readJFRStream()
}

fun main() {
  logger.log(INFO) { "JFR Agent Sample..Enter to exit!" }
  readln()
}

/** Adds a periodic event to the JFR stream. */
inline fun <reified T : Event> addPeriodicEvent(event: T) {
  FlightRecorder.addPeriodicEvent(T::class.java) { event.commit() }
}

private fun readJFRStream() {
  try {
    Thread.startVirtualThread {
      val config = Configuration.getConfiguration("profile")

      RecordingStream(config).use {
        it.enable("jdk.CPULoad").withPeriod(Duration.ofSeconds(5))
        it.onEvent("jdk.CPULoad") { event ->
          val cpuLoad = event.getDouble("machineTotal")
          logger.log(INFO) { "CPU Load: %.2f".format(cpuLoad) }
        }

        // Contended classes for more than 10ms
        it.enable("jdk.JavaMonitorEnter").withThreshold(Duration.ofMillis(10))
        it.onEvent("jdk.JavaMonitorEnter") { event ->
          logger.log(INFO) { "Long held Monitor: ${event.getClass("monitorClass")}" }
        }

        it.enable("jdk.GarbageCollection")

        it.enable("jdk.JVMInformation")
        it.onEvent("jdk.JVMInformation") { event ->
          val jvmName = event.getString("jvmName")
          val jvmVersion = event.getString("jvmVersion")
          logger.log(INFO) { "JVM: $jvmName, Version: $jvmVersion" }
        }

        // AgentType is enabled by default
        it.onEvent("dev.suresh.agent.AgentType") { event ->
          val duration = event.duration.toMillis()
          logger.log(INFO) { "Agent Type: ${event.getString("type")}, duration: $duration" }

          // Find correlation events by getting an event window 1 sec before and after the event.
          if (duration > 500) {
            EventStream.openRepository().use { es ->
              es.setStartTime(event.startTime.minus(Duration.ofSeconds(1)))
              es.setEndTime(event.endTime.plus(Duration.ofSeconds(1)))
              es.onEvent("jdk.GCPhasePause") { gcEvent ->
                val gcDuration = gcEvent.duration.toMillis()
                logger.log(INFO) { "GC pause of $gcDuration millis during the agentType event!" }
              }
            }
          }
        }

        it.start()
      }
    }
  } catch (ex: Exception) {
    logger.log(ERROR, "Unable to attach the JFR monitor", ex)
  }
}
