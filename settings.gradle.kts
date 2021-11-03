rootProject.name = "openjdk-playground"

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Centralizing repositories declaration
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
  // repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

pluginManagement {
  includeBuild("build-logic")

  //  val pluginVersion: String by settings
  //  plugins {
  //    id("com.example.hello") version pluginVersion
  //  }

  repositories {
    gradlePluginPortal()
    google()
    maven(url = uri("https://maven.pkg.jetbrains.space/public/p/jb-coverage/maven"))
    mavenCentral()
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
