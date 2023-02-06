pluginManagement {
  includeBuild("../gradle/build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories { mavenCentral() }
  versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "preview-features"

include("ffm-api")
