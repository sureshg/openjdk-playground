import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  alias(libs.plugins.jte)
  alias(libs.plugins.bestpractices)
  // alias(libs.plugins.kotlin.dsl)
}

/**
 * Java version used in Java toolchains and Kotlin compile JVM target for Gradle precompiled script
 * plugins.
 */
val dslJavaVersion = libs.versions.kotlin.dsl.jvmtarget

java { toolchain { languageVersion = dslJavaVersion.map(JavaLanguageVersion::of) } }

tasks {
  withType<KotlinCompile>().configureEach {
    compilerOptions { jvmTarget = dslJavaVersion.map(JvmTarget::fromTarget) }
  }
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
      displayName = "Gradle Enterprise"
      description = "Gradle enterprise settings plugin re-exposed from dependency"
    }

    // A generic plugin for both project and settings
    create("Generic Plugin") {
      id = "dev.suresh.gradle.plugins.generic"
      implementationClass = "plugins.GenericPlugin"
      displayName = "Generic plugin"
      description = "A plugin-aware generic plugin"
      tags = listOf("Generic Plugin")
    }

    // Uncomment the id to change plugin id for this pre-compiled plugin
    named("plugins.common") {
      // id = "dev.suresh.gradle.plugins.common"
      displayName = "Common plugin"
      description = "Common pre-compiled script plugin"
      tags = listOf("Common Plugin")
    }

    // val settingsPlugin by creating {}
  }
}

// Jte is used for generating build config.
jte {
  contentType = gg.jte.ContentType.Plain
  sourceDirectory = sourceSets.main.get().resources.srcDirs.firstOrNull()?.toPath()
  generate()
}

dependencies {
  implementation(platform(libs.kotlin.bom))
  implementation(kotlin("stdlib"))
  // Hack to access version catalog from pre-compiled script plugins.
  // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.ajalt.mordant)
  implementation(libs.jte.runtime)
  implementation(libs.build.zip.prefixer)
  // compileOnly(libs.jte.kotlin)

  // External plugins deps to use in precompiled script plugins
  // https://docs.gradle.org/current/userguide/custom_plugins.html#applying_external_plugins_in_precompiled_script_plugins
  implementation(libs.build.kotlin)
  // OR implementation(kotlin("gradle-plugin"))
  implementation(libs.build.dokka)
  implementation(libs.build.gradle.enterprise)
  implementation(libs.build.nexus.plugin)
  implementation(libs.build.spotless.plugin)
  implementation(libs.build.shadow.plugin)
  implementation(libs.build.semver.plugin)
  implementation(libs.build.benmanesversions)
  implementation(libs.build.taskinfo)
  implementation(libs.build.dependencyanalysis)
  implementation(libs.build.cyclonedx.plugin)
  implementation(libs.build.foojay.resolver)
  testImplementation(gradleTestKit())

  // implementation(libs.build.jte.plugin)
  // implementation(libs.build.openrewrite.plugin)
  // implementation(libs.build.cp.collisiondetector)
  // implementation(libs.build.maven.plugindev)
  // implementation(libs.build.includegit.plugin)
}
