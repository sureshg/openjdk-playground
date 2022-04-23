rootProject.name = "openjdk-playground"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

includeBuild("build-logic")

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
//    id("plugins.common") // From build-logic
//    kotlin("multiplatform") version kotlinVersion
//    id("org.jetbrains.compose") version composeVersion
//  }

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        // maven(url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev"))
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

plugins {
    id("com.gradle.enterprise") version System.getProperty("gradleEnterprise")
}

// Composite Builds
// includeBuild("ksp-app")
// includeBuild("path-to-repo-clone")

// Add a project
// include("lib")
// project(":lib").projectDir = file("ksp/lib")
