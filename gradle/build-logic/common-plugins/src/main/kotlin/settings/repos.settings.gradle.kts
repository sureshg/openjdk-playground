package settings

import GithubAction
import com.gradle.develocity.agent.gradle.scan.PublishedBuildScan
import org.gradle.kotlin.dsl.*
import org.gradle.toolchains.foojay.FoojayToolchainResolver

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
  id("com.gradle.develocity")
  // Toolchains resolver using the Foojay Disco API.
  id("org.gradle.toolchains.foojay-resolver")
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

@Suppress("UnstableApiUsage")
toolchainManagement {
  jvm {
    javaRepositories {
      repository("foojay") { resolverClass = FoojayToolchainResolver::class.java }
    }
  }
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"

    capture {
      buildLogging = false
      testLogging = false
    }

    obfuscation {
      ipAddresses { it.map { _ -> "0.0.0.0" } }
      hostname { "*******" }
      username { name -> name.reversed() }
    }

    publishing.onlyIf { GithubAction.isEnabled }
    uploadInBackground = false
    tag("GITHUB_ACTION")
    buildScanPublished { addJobSummary() }
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
