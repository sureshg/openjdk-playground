plugins {
  `kotlin-dsl`
}

kotlin {
  sourceSets {
    main {
      kotlin.srcDirs("src")
      languageSettings.apply {
        useExperimentalAnnotation("kotlin.RequiresOptIn")
        useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
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
  implementation(kotlin("reflect"))
}
