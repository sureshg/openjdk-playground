package plugins

import dev.suresh.gradle.*
import org.gradle.jvm.toolchain.*
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
    languageVersion.set(JavaLanguageVersion.of(javaVersion))
    vendor.set(JvmVendorSpec.ORACLE)
  }
}

tasks {
  // Configure "compileJava" and "compileTestJava" tasks.
  withType<JavaCompile>().configureEach {
    options.apply {
      encoding = "UTF-8"
      release.set(javaVersion)
      isIncremental = true
      isFork = true
      debugOptions.debugLevel = "source,lines,vars"
      // For Gradle worker daemon.
      forkOptions.jvmArgs?.addAll(jvmArguments)
      compilerArgs.addAll(
          listOf(
              "--enable-preview",
              "-Xlint:all",
              "-parameters",
              "--add-modules=$addModules",
              // "-Xlint:-deprecation", // suppress deprecations
              // "-Werror",             // treat warnings as errors
              // "-XX:+IgnoreUnrecognizedVMOptions",
              // "--add-exports",
              // "java.base/sun.nio.ch=ALL-UNNAMED",
              // "--patch-module",
              // "$moduleName=${sourceSets.main.get().output.asPath}"
          ),
      )
    }
  }

  /* Configure "compileKotlin" and "compileTestKotlin" tasks.
   * JVM backend compiler options can be found in,
   * https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JVMCompilerArguments.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/config.jvm/src/org/jetbrains/kotlin/config/JvmTarget.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/util/src/org/jetbrains/kotlin/config/ApiVersion.kt#L35
   */
  withType<KotlinCompile>().configureEach {
    usePreciseJavaTracking = true
    kotlinOptions {
      verbose = true
      jvmTarget = kotlinJvmTarget
      javaParameters = true
      incremental = true
      allWarningsAsErrors = false
      freeCompilerArgs +=
          listOf(
              "-Xadd-modules=$addModules",
              "-Xjsr305=strict",
              "-Xjvm-default=all",
              "-Xassertions=jvm",
              "-Xallow-result-return-type",
              "-Xemit-jvm-type-annotations",
              "-Xjspecify-annotations=strict",
              // "-Xjdk-release=$javaVersion",
              // "-Xuse-k2",
              // "-Xbackend-threads=4",
              // "-Xadd-modules=ALL-MODULE-PATH",
              // "-Xmodule-path=",
              // "-Xjvm-enable-preview",
              // "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
              // "-Xexplicit-api={strict|warning|disable}",
              // "-Xgenerate-strict-metadata-version",
          )
    }
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
