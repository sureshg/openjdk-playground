import dev.suresh.gradle.dependencyPath

plugins {
  id("plugins.kotlin")
  application
  alias(libs.plugins.shadow)
}

group = libs.versions.group.get()

version = libs.versions.module.jdk.get()

application { mainClass = "$group.agent.JFRAgentKt" }

tasks {
  jar {
    manifest {
      attributes(
          "Premain-Class" to application.mainClass,
          "Agent-Class" to application.mainClass,
          "Launcher-Agent-Class" to application.mainClass,
          "Can-Redefine-Classes" to "true",
          "Can-Retransform-Classes" to "true",
          "Can-Set-Native-Method-Prefix" to "true",
          "Implementation-Title" to project.name,
          "Implementation-Version" to version,
      )
    }
  }

  // Shows how to use the java agent from a dependency. Make sure to add the allocation agent to
  // dependency block. The agent is added to jvm args in the doFirst block to defer the resolution
  // of the configuration to Gradle’s task execution phase.
  run.invoke {
    doFirst {
      val agentEnabled = args.orEmpty().contains("--allocation-agent")
      if (agentEnabled) {
        println("Adding allocation agent to jvm args...")
        val agentJar = dependencyPath(libs.javaagent.allocation.get())
        application.applicationDefaultJvmArgs += listOf("-javaagent:$agentJar")
      }
    }
  }
}

// dependencies { implementation(libs.javaagent.allocation) }
