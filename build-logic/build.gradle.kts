plugins {
    //`kotlin-dsl`
    kotlin("jvm") version "1.4.30"
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs("src")
        }
    }
}

//kotlinDslPluginOptions {
//    experimentalWarning.set(false)
//}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}