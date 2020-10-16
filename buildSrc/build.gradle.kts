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

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
