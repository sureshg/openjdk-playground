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
          "Implementation-Title" to "JFR Agent",
          "Implementation-Version" to version,
      )
    }
  }
}
