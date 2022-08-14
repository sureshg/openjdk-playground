pluginManagement {
  // Included plugin builds can contribute settings and project plugins
  includeBuild("build-logic")

  // Configures default plugin versions
  val gradleEnterprise: String by settings
  // val kotlinVersion = extra["kotlin.version"] as String
  plugins {
    id("com.gradle.enterprise") version gradleEnterprise
    // kotlin("jvm") version kotlinVersion
    // id("plugins.common-plugins") // From build-logic
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "kotlinx-atomicfu" ->
            useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
        "app.cash.licensee" ->
            useModule("app.cash.licensee:licensee-gradle-plugin:${requested.version}")
        "io.reflekt" -> useModule("io.reflekt:gradle-plugin:${this.requested.version}")
      }
    }
  }

  // Plugin repositories to use
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

// Apply the plugins to all projects
plugins {
  id("com.gradle.enterprise")
  // id("plugins.common-settings")
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
includeBuild("build-logic") // includeBuild("path-to-repo-clone")

// Add modules
// include("bom")
// include("core")
// project(":lib").projectDir = file("ksp/lib")

// extra.properties.forEach { (k, v) ->
//   println("$k -> $v")
// }
