plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val kotlinVersion: String = System.getProperty("KotlinVersion")
dependencies {
    implementation(kotlin("gradle-plugin-api", kotlinVersion))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
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