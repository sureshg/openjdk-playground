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

gradlePlugin {
  plugins {
    create("dev.suresh.gradle.settings") {
      id = "dev.suresh.gradle.settings"
      implementationClass = "plugins.SettingsPlugin"
    }

    // Re-exposure of plugin from dependency. Gradle doesn't expose the plugin itself.
    create("com.gradle.enterprise") {
      id = "com.gradle.enterprise"
      implementationClass = "com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin"
      dependencies { implementation(libs.gradle.enterprise) }
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
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.ajalt.mordant)

  // External plugins deps to use in precompiled script plugins
  implementation(libs.nexus.plugin)
  implementation(libs.spotless.plugin)
}
