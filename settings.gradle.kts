rootProject.name = "openjdk-playground"

// Centralizing repositories declaration
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        jcenter()
        google()
        maven(url = "https://kotlin.bintray.com/kotlinx/")
    }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

// For plugin EAP versions
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        // mavenLocal()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
            }
        }
    }
}

// Composite Builds
includeBuild("build-logic")
