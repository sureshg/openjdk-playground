@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }

  includeBuild("../build-logic")
}

dependencyResolutionManagement {
  repositories { mavenCentral() }
  versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

rootProject.name = "panama-api"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("ffm-api")
