plugins {
    `kotlin-dsl`
}

kotlin {
    sourceSets {
        main {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(libs.ajalt.mordant)
    // Hack to access libs catalog from pre-compiled script plugins.
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
