rootProject.name = "openjdk-latest"

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
                "symbol-processing" ->
                    useModule("com.google.devtools.ksp:symbol-processing:${requested.version}")
            }
        }
    }
}

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
