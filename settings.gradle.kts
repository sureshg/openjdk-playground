pluginManagement {

  // Included plugin builds can contribute settings and project plugins
  includeBuild("gradle/build-logic")

  // val gradleEnterprise: String by settings //OR
  // val gradleEnterprise =
  //      file("$rootDir/gradle/libs.versions.toml")
  //          .readLines()
  //          .first { it.contains("gradle-enterprise") }
  //          .split("\"")[1]

  // https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_version_management
  plugins {
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

// With composite builds, the module dependencies to 'preview-features' will always
// be substituted with project dependencies.
includeBuild("preview-features")
