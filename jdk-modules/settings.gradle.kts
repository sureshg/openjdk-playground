pluginManagement {
  includeBuild("../gradle/build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories { mavenCentral() }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  versionCatalogs { create("libs") { from(files("../gradle/libs.versions.toml")) } }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "jdk-modules"

include("ffm-api")

include("jvm-agent")
