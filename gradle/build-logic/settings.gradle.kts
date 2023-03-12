@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

  versionCatalogs {
    // Use the main version catalog
    create("libs") { from(files("../libs.versions.toml")) }
  }
}

// apply(from = "path/to/repos.settings.gradle.kts")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "build-logic"

include("common-plugins")
