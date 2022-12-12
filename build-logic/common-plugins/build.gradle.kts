@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  alias(libs.plugins.jte)
  alias(libs.plugins.benmanes)
}

kotlin {
  sourceSets.all {
    languageSettings.apply {
      progressiveMode = true
      optIn("kotlin.ExperimentalStdlibApi")
      optIn("kotlin.io.path.ExperimentalPathApi")
      optIn("kotlin.time.ExperimentalTime")
    }
  }
}

gradlePlugin {
  plugins {
    create("dev.suresh.gradle.settings") {
      id = "dev.suresh.gradle.settings"
      implementationClass = "plugins.SettingsPlugin"
      description = "Gradle settings plugin with build scan TOS accepted"
    }

    // Re-exposure of plugin from dependency. Gradle doesn't expose the plugin itself.
    create("com.gradle.enterprise") {
      id = "com.gradle.enterprise"
      implementationClass = "com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin"
      dependencies { implementation(libs.build.gradle.enterprise) }
      description = "Gradle enterprise settings plugin re-exposed from dependency"
    }

    // val settingsPlugin by this.creating {}
  }
}

// Jte is used for generating build config.
jte {
  contentType.set(gg.jte.ContentType.Plain)
  sourceDirectory.set(sourceSets.main.get().resources.srcDirs.firstOrNull()?.toPath())
  generate()
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(kotlin("stdlib"))
  // Hack to access version catalog from pre-compiled script plugins.
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.ajalt.mordant)
  implementation(libs.jte.runtime)
  // compileOnly(libs.jte.kotlin)

  // External plugins deps to use in precompiled script plugins
  implementation(libs.build.kotlin)
  implementation(libs.build.dokka)
  implementation(libs.build.nexus.plugin)
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.taskinfo)
  implementation(libs.build.dependencyanalysis)
  implementation(libs.build.bestpractices.plugin)

  // implementation(libs.build.jte.plugin)
  // implementation(libs.build.openrewrite.plugin)
  // implementation(libs.build.cp.collisiondetector)
  // implementation(libs.build.maven.plugindev)
}
