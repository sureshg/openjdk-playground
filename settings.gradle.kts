rootProject.name = "openjdk-latest"

/** For plugin EAP versions */
pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
