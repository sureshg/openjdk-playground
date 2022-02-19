rootProject.name = "openjdk-playground"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Centralizing repositories declaration
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
  // repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

// extra.properties.forEach { (k, v) ->
//   println("$k -> $v")
// }
// val ktVersion = extra["kotlin.version"] as String

pluginManagement {
  includeBuild("build-logic")

//  val kotlinVersion: String by settings
//  val composeVersion: String by settings
//  plugins {
//    kotlin("multiplatform") version kotlinVersion
//    id("org.jetbrains.compose") version composeVersion
//  }

  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    // maven(url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev"))
    // maven(url = uri("https://packages.jetbrains.team/maven/p/reflekt/reflekt"))
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
        "app.cash.licensee" -> useModule("app.cash.licensee:licensee-gradle-plugin:${requested.version}")
        "io.reflekt" -> useModule("io.reflekt:gradle-plugin:${this.requested.version}")
      }
    }
  }
}

// Composite Builds
// includeBuild("ksp-app")
// includeBuild("path-to-repo-clone")

// Add a project
// include("lib")
// project(":lib").projectDir = file("ksp/lib")
