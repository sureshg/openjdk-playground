import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  // alias(libs.plugins.kotlin.dsl)
  alias(libs.plugins.jte)
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
    // Re-exposure of plugin from dependency. Gradle doesn't expose the plugin itself.
    create("com.gradle.enterprise") {
      id = "com.gradle.enterprise"
      implementationClass = "com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin"
      dependencies { implementation(libs.build.gradle.enterprise) }
      description = "Gradle enterprise settings plugin re-exposed from dependency"
    }

    // A sample settings plugin to configure build scan TOS
    create("dev.suresh.gradle.settings") {
      id = "dev.suresh.gradle.settings"
      implementationClass = "plugins.SettingsPlugin"
      description = "Gradle settings plugin with build scan TOS accepted"
    }

    // val settingsPlugin by this.creating {}
  }
}

// Jte is used for generating build config.
jte {
  contentType = gg.jte.ContentType.Plain
  sourceDirectory = sourceSets.main.get().resources.srcDirs.firstOrNull()?.toPath()
  generate()
}

tasks {
  withType<KotlinCompile>().configureEach {
    compilerOptions { jvmTarget = JvmTarget.fromTarget(libs.versions.kotlin.dsl.jvmtarget.get()) }
  }
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
  // https://docs.gradle.org/current/userguide/custom_plugins.html#applying_external_plugins_in_precompiled_script_plugins
  implementation(libs.build.kotlin)
  // OR implementation(kotlin("gradle-plugin"))
  implementation(libs.build.dokka)
  implementation(libs.build.nexus.plugin)
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.taskinfo)
  implementation(libs.build.dependencyanalysis)
  implementation(libs.build.bestpractices.plugin)
  implementation(libs.build.cyclonedx.plugin)
  testImplementation(gradleTestKit())

  // implementation(libs.build.jte.plugin)
  // implementation(libs.build.openrewrite.plugin)
  // implementation(libs.build.cp.collisiondetector)
  // implementation(libs.build.maven.plugindev)
}
