package settings

import GithubAction
import com.gradle.scan.plugin.PublishedBuildScan
import org.gradle.kotlin.dsl.*

pluginManagement {
  require(JavaVersion.current().isJava11Compatible) {
    "This build requires Gradle to be run with at least Java 11"
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
  // Gradle build scan
  id("com.gradle.enterprise")

  // Use semver on all projects
  id("com.javiersc.semver")

  // Include plugin-aware generic plugin
  id("dev.suresh.gradle.plugins.generic")

  // Include another pre-compiled settings plugin
  id("settings.include")
}

// Centralizing repositories declaration
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    capture.isTaskInputFiles = true
    if (GithubAction.isEnabled) {
      publishAlways()
      isUploadInBackground = false
      tag("GITHUB_ACTION")
      buildScanPublished { addJobSummary() }
    }
  }
}

/** Add build scan details to GitHub Job summary report! */
fun PublishedBuildScan.addJobSummary() =
    with(GithubAction) {
      setOutput("build_scan_uri", buildScanUri)
      addJobSummary(
          """
      | ##### 🚀 Gradle BuildScan [URL](${buildScanUri.toASCIIString()})
      """
              .trimMargin())
    }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
