package settings

import GithubAction
import com.gradle.scan.plugin.PublishedBuildScan
import org.gradle.kotlin.dsl.*

// Apply the plugins to all projects
plugins {
  id("com.gradle.enterprise")
  // Include another setting plugin
  id("settings.include")
  // id("dev.suresh.gradle.settings")
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
fun PublishedBuildScan.addJobSummary() {
  with(GithubAction) {
    setOutput("build_scan_uri", buildScanUri)
    addJobSummary(
        """
        | ##### 🚀 Gradle BuildScan [URL](${buildScanUri.toASCIIString()})
        """
            .trimMargin())
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
