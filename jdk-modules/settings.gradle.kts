pluginManagement {
  includeBuild("../gradle/build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

plugins { id("settings.repos") }

dependencyResolutionManagement.versionCatalogs {
  create("libs") { from(files("../gradle/libs.versions.toml")) }
}

rootProject.name = "jdk-modules"

include("ffm-api")

include("jvm-agent")

include("jvm-wasm")
