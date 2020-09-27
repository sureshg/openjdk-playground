rootProject.name = "openjdk-latest"

/** For plugin EAP versions */
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
