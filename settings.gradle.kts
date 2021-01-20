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

    // For KSP
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "symbol-processing" -> useModule("com.google.devtools.ksp:symbol-processing:${requested.version}")
                "binary-compatibility-validator" -> useModule("org.jetbrains.kotlinx:binary-compatibility-validator:${requested.version}")
            }
        }
    }
}
