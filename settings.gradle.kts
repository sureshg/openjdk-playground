pluginManagement {
  // Included plugin builds can contribute settings and project plugins
  includeBuild("build-logic")

  // https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_version_management
  plugins {
    // val gradleEnterprise: String by settings
    // id("com.gradle.enterprise") version gradleEnterprise

    // val kotlinVersion = extra["kotlin.version"] as String
    // kotlin("jvm") version kotlinVersion
  }

  // Plugin repositories to use
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "kotlinx-atomicfu" ->
            useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
        "app.cash.licensee" ->
            useModule("app.cash.licensee:licensee-gradle-plugin:${requested.version}")
      }
    }
  }
}

// Apply the plugins to all projects
plugins {
  id("com.gradle.enterprise")
  id("dev.suresh.gradle.settings")
  // id("plugins.common")
}

// Centralizing repositories declaration
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

rootProject.name = "openjdk-playground"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Composite Builds
includeBuild(
    "build-logic")

// Add modules
// include("bom")
// include("core")
// project(":lib").projectDir = file("ksp/lib")

// extra.properties.forEach { (k, v) ->
//   println("$k -> $v")
// }
