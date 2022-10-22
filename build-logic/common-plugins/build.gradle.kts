@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  `kotlin-dsl`
  alias(libs.plugins.jte)
  alias(libs.plugins.benmanes)
}

kotlin {
  sourceSets {
    main {
      languageSettings.apply {
        progressiveMode = true
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
      dependencies { implementation(libs.build.gradle.enterprise) }
    }
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
  implementation(kotlin("stdlib-jdk8"))
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
  implementation(libs.build.benmanesversions)
  implementation(libs.build.taskinfo)
  implementation(libs.build.dependencyanalysis)
  implementation(libs.build.bestpractices.plugin)
  // implementation(libs.build.jte.plugin)
}

// https://handstandsam.com/2022/04/13/using-the-kotlin-dsl-gradle-plugin-forces-kotlin-1-4-compatibility/
// afterEvaluate {
//   tasks.withType<KotlinCompile>().configureEach {
//     kotlinOptions {
//       apiVersion = libs.versions.kotlindsl.api.version.get()
//       languageVersion = libs.versions.kotlindsl.api.version.get()
//       // suppress warnings for jte codegen.
//       suppressWarnings = true
//     }
//   }
// }
