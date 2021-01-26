rootProject.name = "openjdk-latest"

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
        jcenter()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "kotlinx-atomicfu" -> useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
                "binary-compatibility-validator" -> useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
            }
        }
    }
}

// Composite Builds
// includeBuild("build-config")
