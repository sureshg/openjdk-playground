package plugins

import dev.suresh.gradle.*
import kotlinx.validation.ApiValidationExtension
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `java-library`
  id("com.google.devtools.ksp")
  kotlin("jvm")
  kotlin("plugin.serialization")
  id("kotlinx-atomicfu")
  id("dev.zacsweers.redacted")
  id("org.jetbrains.dokka")
  id("org.jetbrains.kotlinx.kover")
}

// Apply bincompat validation only to the root project.
if (project == rootProject) {
  apply(plugin = "org.jetbrains.kotlinx.binary-compatibility-validator")
}

java {
  withSourcesJar()
  withJavadocJar()

  toolchain {
    languageVersion = toolchainVersion
    vendor = toolchainVendor
  }
}

kotlin {
  sourceSets.all {
    languageSettings.apply {
      progressiveMode = true
      languageVersion = kotlinLangVersion.get().version
      optIn("kotlin.ExperimentalStdlibApi")
      optIn("kotlin.contracts.ExperimentalContracts")
      optIn("kotlin.ExperimentalUnsignedTypes")
      optIn("kotlin.io.path.ExperimentalPathApi")
      optIn("kotlin.time.ExperimentalTime")
      optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
      optIn("kotlinx.serialization.ExperimentalSerializationApi")
      optIn("kotlin.ExperimentalMultiplatform")
      optIn("kotlin.js.ExperimentalJsExport")
      // enableLanguageFeature("DataObjects")
      // optIn("kotlinx.coroutines.FlowPreview")
    }
    // kotlin.setSrcDirs(listOf("src/kotlin"))
  }

  jvmToolchain {
    languageVersion = toolchainVersion
    vendor = toolchainVendor
  }

  // jvmToolchain(javaVersion.map { it.majorVersion.toInt() }.get())
  // kotlinDaemonJvmArgs = jvmArguments

  // explicitApi()
  // sourceSets { main { ... } }
}

atomicfu {
  jvmVariant = "VH"
  transformJvm = true
  verbose = true
}

ksp {
  arg("autoserviceKsp.verify", "true")
  arg("autoserviceKsp.verbose", "true")
  // arg(KspArgsProvider(project.layout.projectDirectory.file("config").asFile))
}

redacted { enabled = true }

// Configure bincompat validation only if the plugin is applied to the root project.
plugins.withId("org.jetbrains.kotlinx.binary-compatibility-validator") {
  extensions.configure<ApiValidationExtension>("apiValidation") { validationDisabled = true }
}

kover {
  // useJacoco()
}

koverReport {
  defaults {
    filters { excludes { classes("dev.suresh.example.*") } }
    html { title = "${project.name} code coverage report" }
  }
}

tasks {
  // Configure "compileJava" and "compileTestJava" tasks.
  withType<JavaCompile>().configureEach {
    options.apply {
      encoding = "UTF-8"
      release = javaRelease
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
                  // "-Xlint:lossy-conversions", // suppress lossy conversions
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
   *
   * Gradle Kotlin DSL Api/Lang versions,
   * https://github.com/gradle/gradle/blob/master/subprojects/kotlin-dsl-plugins/src/main/kotlin/org/gradle/kotlin/dsl/plugins/dsl/KotlinDslCompilerPlugins.kt#L67-L68
   */
  withType<KotlinCompile>().configureEach {
    usePreciseJavaTracking = true
    compilerOptions {
      jvmTarget = kotlinJvmTarget
      apiVersion = kotlinApiVersion
      languageVersion = kotlinLangVersion
      verbose = true
      javaParameters = true
      allWarningsAsErrors = false
      suppressWarnings = false
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

dependencies {
  implementation(platform(libs.kotlin.bom))
  implementation(kotlin("stdlib"))
  // implementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.datetime)
  // compileOnly(libs.kotlinx.atomicfu)

  // Auto-service
  ksp(libs.ksp.auto.service)
  implementation(libs.google.auto.annotations)
  // kapt("com.google.auto.service:auto-service:1.0.1")

  testImplementation(platform(libs.junit.bom))
  testImplementation(kotlin("test-junit5"))
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.lincheck)
  testImplementation(libs.kotest.core)
  testImplementation(libs.kotest.junit5)
  testImplementation(libs.slf4j.simple)
  testImplementation(libs.mockk)
}
