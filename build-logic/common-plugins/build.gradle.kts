plugins { `kotlin-dsl` }

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
  // Hack to access libs catalog from pre-compiled script plugins.
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.ajalt.mordant)

  // External plugins deps to use in precompiled script plugins
  implementation(libs.nexus.plugin)
  implementation(libs.spotless.plugin)
}
