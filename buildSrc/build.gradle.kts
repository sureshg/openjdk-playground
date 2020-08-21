plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
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

// Required if want to use latest kotlin
// val kotlinVersion: String = System.getProperty("KotlinVersion")
// dependencies {
//    implementation(kotlin("stdlib-jdk8", kotlinVersion))
//    implementation(kotlin("reflect", kotlinVersion))
//    implementation(kotlin("gradle-plugin-api", kotlinVersion))
// }