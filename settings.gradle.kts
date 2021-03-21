rootProject.name = "openjdk-playground"

// Centralizing repositories declaration
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

// For plugin EAP versions
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
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
