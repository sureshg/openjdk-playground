rootProject.name = "openjdk-playground"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Centralizing repositories declaration
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
  // repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

val description: String by settings
println(description)

pluginManagement {
  // Included plugin builds can contribute settings and project plugins
  includeBuild("build-logic")

  // Plugin repositories to use
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }

  // Configures default plugin versions
  plugins {
    // val kotlinVersion = extra["kotlin.version"] as String
    // kotlin("multiplatform") version kotlinVersion
    // kotlin("jvm") version kotlinVersion
    // kotlin("js") version kotlinVersion
    // kotlin("plugin.serialization") version kotlinVersion
    // id("plugins.common") // From build-logic
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
        "app.cash.licensee" -> useModule("app.cash.licensee:licensee-gradle-plugin:${requested.version}")
        "io.reflekt" -> useModule("io.reflekt:gradle-plugin:${this.requested.version}")
      }
    }
  }
}

plugins {
  id("com.gradle.enterprise") version System.getProperty("gradleEnterprise")
}

// Composite Builds
includeBuild("build-logic")
// includeBuild("path-to-repo-clone")

// Add a project
// include("lib")
// project(":lib").projectDir = file("ksp/lib")

// extra.properties.forEach { (k, v) ->
//   println("$k -> $v")
// }
