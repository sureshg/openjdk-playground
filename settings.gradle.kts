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

// For plugin EAP versions
pluginManagement {
  includeBuild("build-logic")
  repositories {
    gradlePluginPortal()
    google()
    maven(url = uri("https://maven.pkg.jetbrains.space/public/p/jb-coverage/maven"))
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

// Add a project
// include("lib")
// project(":lib").projectDir = file("ksp/lib")
