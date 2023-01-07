package plugins

import dev.suresh.gradle.*
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-library`
  kotlin("jvm")
}

java {
  withSourcesJar()
  withJavadocJar()

  toolchain {
    languageVersion.set(toolchainVersion)
    vendor.set(toolchainVendor)
  }
}

kotlin {
  sourceSets.all {
    languageSettings.apply {
      progressiveMode = true
      enableLanguageFeature("DataObjects")
      optIn("kotlin.ExperimentalStdlibApi")
      optIn("kotlin.ExperimentalUnsignedTypes")
      optIn("kotlin.io.path.ExperimentalPathApi")
      optIn("kotlin.time.ExperimentalTime")
      optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
      optIn("kotlinx.coroutines.FlowPreview")
      optIn("kotlinx.serialization.ExperimentalSerializationApi")
      optIn("kotlin.ExperimentalMultiplatform")
      optIn("kotlin.js.ExperimentalJsExport")
    }
    // kotlin.setSrcDirs(listOf("src/kotlin"))
  }

  jvmToolchain {
    languageVersion.set(toolchainVersion)
    vendor.set(toolchainVendor)
  }

  // jvmToolchain(javaVersion.map { it.majorVersion.toInt() }.get())
  // kotlinDaemonJvmArgs = jvmArguments

  // explicitApi()
  // sourceSets { main { ... } }
}

tasks {
  // Configure "compileJava" and "compileTestJava" tasks.
  withType<JavaCompile>().configureEach {
    options.apply {
      encoding = "UTF-8"
      release.set(javaRelease)
      isIncremental = true
      isFork = true
      debugOptions.debugLevel = "source,lines,vars"
      // For Gradle worker daemon.
      forkOptions.jvmArgs?.addAll(jvmArguments)
      compilerArgs.addAll(
          jvmArguments +
              listOf(
                  "-Xlint:all",
                  "-parameters",
                  "--add-modules=$addModules",
                  // "-Xlint:-deprecation", // suppress deprecations
                  // "-XX:+IgnoreUnrecognizedVMOptions",
                  // "--add-exports",
                  // "java.base/sun.nio.ch=ALL-UNNAMED",
                  // "--patch-module",
                  // "$moduleName=${sourceSets.main.get().output.asPath}"
              ),
      )
    }
  }

  /*
   * JVM backend compiler options can be found in,
   * https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JVMCompilerArguments.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/config.jvm/src/org/jetbrains/kotlin/config/JvmTarget.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/util/src/org/jetbrains/kotlin/config/ApiVersion.kt#L35
   */
  withType<KotlinCompile>().configureEach {
    usePreciseJavaTracking = true
    compilerOptions {
      jvmTarget.set(kotlinJvmTarget)
      apiVersion.set(kotlinApiVersion)
      languageVersion.set(kotlinLangVersion)
      useK2.set(k2Enabled)
      verbose.set(true)
      javaParameters.set(true)
      allWarningsAsErrors.set(false)
      suppressWarnings.set(false)
      freeCompilerArgs.addAll(
          "-Xadd-modules=$addModules",
          "-Xjsr305=strict",
          "-Xjvm-default=all",
          "-Xassertions=jvm",
          "-Xcontext-receivers",
          "-Xallow-result-return-type",
          "-Xemit-jvm-type-annotations",
          "-Xjspecify-annotations=strict",
          "-Xextended-compiler-checks",
          "-Xuse-fir-extended-checkers",
          // "-Xjdk-release=$javaVersion",
          // "-Xadd-modules=ALL-MODULE-PATH",
          // "-Xmodule-path=",
          // "-Xjvm-enable-preview",
          // "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
          // "-Xexplicit-api={strict|warning|disable}",
          // "-Xgenerate-strict-metadata-version",
      )
    }
    // finalizedBy("spotlessApply")
  }

  register<Exec>("buildUniversalMacOsBinary") {
    dependsOn("buildMacosX64", "buildMacosArm64")
    commandLine(
        "lipo",
        "-create",
        "-output",
        "build/${rootProject.name}",
        "build/macosX64/${rootProject.name}",
        "build/macosArm64/${rootProject.name}")
    workingDir = buildDir
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Builds universal macOS binary"
  }
}
