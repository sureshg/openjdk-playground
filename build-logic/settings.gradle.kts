@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories.gradlePluginPortal()
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include("common-plugins")
