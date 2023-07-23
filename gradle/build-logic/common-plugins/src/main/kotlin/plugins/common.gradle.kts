package plugins

import GithubAction
import com.github.ajalt.mordant.rendering.TextColors
import dev.suresh.gradle.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.spi.*
import org.gradle.api.tasks.testing.logging.*
import org.gradle.internal.os.OperatingSystem
import org.gradle.kotlin.dsl.*
import tasks.*

plugins {
  idea
  java
  application
  `test-suite-base`
  // `java-library`
  // `java-test-fixtures`
}

if (hasCleanTask) {
  logger.warn(
      TextColors.yellow(
              """
      | CLEANING ALMOST NEVER FIXES YOUR BUILD!
      | Cleaning is often a last-ditch effort to fix perceived build problems that aren't going to
      | actually be fixed by cleaning. What cleaning will do though is make your next few builds
      | significantly slower because all the incremental compilation data has to be regenerated,
      | so you're really just making your day worse.
      """)
          .trimMargin(),
  )
}

// Print all the tasks
printTaskGraph()

// Access version catalogs´
printVersionCatalog()

// After the project configure
afterEvaluate { println("=== Project Configuration Completed ===") }
// apply(from ="")

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
  project.vcs = "Git"
}

java {
  withSourcesJar()
  withJavadocJar()

  toolchain {
    languageVersion = toolchainVersion
    vendor = toolchainVendor
  }
  // modularity.inferModulePath = true
}

// For dependencies that are needed for development only.
val devOnly: Configuration by configurations.creating

@Suppress("UnstableApiUsage")
testing {
  suites {
    val test by getting(JvmTestSuite::class) { useJUnitJupiter(libs.versions.junit.asProvider()) }
    // OR "test"(JvmTestSuite::class) {}

    // Configure all test suites
    withType(JvmTestSuite::class) {
      targets.configureEach {
        testTask {
          jvmArgs(jvmArguments)
          classpath += devOnly
          reports.html.required = true
          maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1

          testLogging {
            events =
                setOf(
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.FAILED,
                    TestLogEvent.SKIPPED,
                )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
          }
        }
      }
    }
  }
}

tasks {

  // Prints java module dependencies using jdeps
  val printModuleDeps by registering {
    description = "Print Java Platform Module dependencies of the application."
    group = LifecycleBasePlugin.BUILD_TASK_NAME

    doLast {
      val jarTask = named("shadowJar", Jar::class)
      val jarFile = jarTask.get().archiveFile.get().asFile

      val jdeps =
          ToolProvider.findFirst("jdeps").orElseGet { error("jdeps tool is missing in the JDK!") }
      val out = StringWriter()
      val pw = PrintWriter(out)
      jdeps.run(
          pw,
          pw,
          "-q",
          "-R",
          "--print-module-deps",
          "--ignore-missing-deps",
          "--multi-release=${javaRelease.get()}",
          jarFile.absolutePath,
      )

      val modules = out.toString()
      logger.quiet(
          """
          |Application modules for OpenJDK-${java.toolchain.languageVersion.get()} are,
          |${modules.split(",")
              .mapIndexed {i, module -> " ${(i+1).toString().padStart(2)}) $module" }
              .joinToString(System.lineSeparator())
             }
          """
              .trimMargin())
    }

    dependsOn("shadowJar")
  }

  val githubActionOutput by registering {
    description = "Set Github workflow action output for this build"
    group = LifecycleBasePlugin.BUILD_TASK_NAME

    doLast {
      val uberJar = named("shadowJar", Jar::class).get().archiveFile.get().asFile
      println("Uber Jar : ${uberJar.path} ${uberJar.displaySize}")
      println(appRunCmd(uberJar, run.get().jvmArgs))

      GithubAction.setOutput("version", project.version)
      GithubAction.setOutput("uberjar_name", uberJar.name)
      GithubAction.setOutput("uberjar_path", uberJar.absolutePath)

      if (OperatingSystem.current().isUnix) {
        val execJar = project.layout.buildDirectory.file(project.name).get().asFile
        GithubAction.setOutput("execjar_name", execJar.name)
        GithubAction.setOutput("execjar_path", execJar.absolutePath)
      }
    }
    dependsOn("shadowJar")
  }

  val buildExecutable by
      registering(ReallyExecJar::class) {
        val shadowJar = named("shadowJar", Jar::class) // project.tasks.shadowJar
        jarFile = shadowJar.flatMap { it.archiveFile }
        // javaOpts = application.applicationDefaultJvmArgs
        javaOpts = run.get().jvmArgs
        onlyIf { OperatingSystem.current().isUnix }
      }

  // val versionCatalog = the<VersionCatalogsExtension>().named("libs")
  val copyTemplates by
      registering(Copy::class) {
        description = "Generate template classes"
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        // GitHub actions workaround
        val props = project.properties.toMutableMap()
        props["git_branch"] = project.findProperty("branch_name")
        props["git_tag"] = project.findProperty("base_tag")

        // Add info from Gradle version catalog
        val versionCatalog = project.catalogs.named("libs")
        props["javaVersion"] = versionCatalog.findVersion("java").get()
        props["kotlinVersion"] = versionCatalog.findVersion("kotlin").get()
        props["gradleVersion"] = versionCatalog.findVersion("gradle").get()

        if (debugEnabled) {
          props.forEach { (t, u) -> println("%1\$-42s --> %2\$s".format(t, u)) }
        }

        filteringCharset = "UTF-8"
        from(project.projectDir.resolve("src/main/templates"))
        into(project.layout.buildDirectory.dir("generated-sources/templates/kotlin/main"))
        exclude { it.name.startsWith("jte") }
        expand(props)

        // inputs.property("buildversions", props.hashCode())
      }

  // Add the generated templates to the source set.
  sourceSets { main { java.srcDirs(copyTemplates) } }

  // jdeprscan task configuration
  val jdepExtn = extensions.create<JdeprscanExtension>("jdeprscan")
  val jdeprscan = register<Jdeprscan>("jdeprscan", jdepExtn)
  jdeprscan {
    val shadowJar by existing(Jar::class)
    jarFile = shadowJar.flatMap { it.archiveFile }
  }

  register("ciBuild") {
    description = "Custom task for GitHub action CI build"
    dependsOn(tasks.run, tasks.build, "koverHtmlReport", "dokkaHtml")
    named("koverHtmlReport").map { it.mustRunAfter(tasks.build) }
    named("dokkaHtml").map { it.mustRunAfter(tasks.build) }
  }

  build { finalizedBy(printModuleDeps, buildExecutable, githubActionOutput) }

  // Task to print the project version
  register("v") {
    description = "Print the ${project.name} version!"
    doLast { println(project.version.toString()) }
  }
}

// Set additional jvm args
// -----------------------
//  tasks.withType<JavaExec>().matching {  }.configureEach {
//    jvmArgs(
//      "--enable-native-access=ALL-UNNAMED",
//      "--add-modules=jdk.incubator.foreign",
//      "--module-path", files(configurations.compileClasspath).asPath,
//      "--add-modules", "ALL-MODULE-PATH"
//    )
//    javaLauncher = project.javaToolchains.launcherFor(java.toolchain)
//  }

// Gradle dependency substitution
// ------------------------------
// allprojects {
//    configurations.all {
//        resolutionStrategy.dependencySubstitution {
//          substitute(module("org.jetbrains.compose.compiler:compiler"))
//              .using(module("androidx.compose.compiler:compiler:$composeCompilerVersion"))
//              .because("using the compose prerelease compiler")
//        }
//    }
// }
