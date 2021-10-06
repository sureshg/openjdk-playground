plugins {
  `kotlin-dsl`
}


kotlin {
  sourceSets {
    main {
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
}
