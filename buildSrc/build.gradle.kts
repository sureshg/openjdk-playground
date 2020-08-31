plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

/** Use latest kotlin */
val kotlinVersion: String = System.getProperty("KotlinVersion") ?: "1.4.0"
dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
    implementation(kotlin("gradle-plugin-api", kotlinVersion))
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

