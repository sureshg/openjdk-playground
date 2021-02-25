plugins {
    `kotlin-dsl`
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs("src")
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
