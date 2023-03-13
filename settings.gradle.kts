pluginManagement {
  // Included plugin builds can contribute settings and project plugins
  includeBuild("gradle/build-logic")

  // Plugin repositories to use
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }

  // val gradleEnterprise: String by settings //OR
  // val gradleEnterprise =
  //      file("$rootDir/gradle/libs.versions.toml")
  //          .readLines()
  //          .first { it.contains("gradle-enterprise") }
  //          .split("\"")[1]

  // https://docs.gradle.org/current/userguide/plugins.html#sec:plugin_version_management
  // plugins {
  //    id("com.gradle.enterprise") version gradleEnterprise
  //    val kotlinVersion = extra["kotlin.version"] as String
  //    kotlin("jvm") version kotlinVersion
  // }
}

// Apply common settings plugins to all projects
plugins { id("settings.repos") }

rootProject.name = "openjdk-playground"

include(":playground-bom")

include("playground-catalog")

include("playground-k8s")
// project(":playground-bom").buildFileName = "bom-build.gradle.kts"
// project(":aaa:xxx").projectDir = file("aaa/bbb")

// With composite builds, the module dependencies to 'jdk-modules'
// will always be substituted with project dependencies.
includeBuild("jdk-modules")
