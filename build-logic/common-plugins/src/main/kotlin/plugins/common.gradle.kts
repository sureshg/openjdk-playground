package plugins

import GithubAction
import dev.suresh.gradle.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.*
import java.util.spi.*
import org.gradle.internal.os.OperatingSystem
import tasks.*

plugins {
  idea
  java
  application
  `test-suite-base`
  // `java-library`
}

if (hasCleanTask) {
  logger.warn(
      """
        CLEANING ALMOST NEVER FIXES YOUR BUILD!
        Cleaning is often a last-ditch effort to fix perceived build problems that aren't going to
        actually be fixed by cleaning. What cleaning will do though is make your next few builds
        significantly slower because all the incremental compilation data has to be regenerated,
        so you're really just making your day worse.
    """.trimIndent(),
  )
}

// Print all the tasks
printTaskGraph()

// Access version catalogs
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
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.asProvider().get()))
    vendor.set(JvmVendorSpec.ORACLE)
  }
  // modularity.inferModulePath.set(true)
}

tasks {
  // Clean all composite builds
  register("cleanAll") {
    description = "Clean all composite builds"
    group = LifecycleBasePlugin.CLEAN_TASK_NAME

    gradle.includedBuilds.forEach { dependsOn(it.task(":clean")) }
  }

  jar { exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA") }

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
          "--multi-release=${java.toolchain.languageVersion.get().asInt()}",
          jarFile.absolutePath,
      )

      val modules = out.toString()
      println(modules)
    }
    dependsOn("shadowJar")
  }

  val githubActionOutput by registering {
    description = "Set Github workflow action output for this build"
    group = LifecycleBasePlugin.BUILD_TASK_NAME

    doLast {
      val uberJar = named("shadowJar", Jar::class).get().archiveFile.get().asFile
      println("Uber Jar : ${uberJar.path} ${uberJar.mebiSize}")
      println(appRunCmd(uberJar, application.applicationDefaultJvmArgs.toList()))

      GithubAction.setOutput("version", project.version)
      GithubAction.setOutput("uberjar_name", uberJar.name)
      GithubAction.setOutput("uberjar_path", uberJar.absolutePath)

      if (OperatingSystem.current().isUnix) {
        val execJar = File(project.buildDir, project.name)
        GithubAction.setOutput("execjar_name", execJar.name)
        GithubAction.setOutput("execjar_path", execJar.absolutePath)
      }
    }
    dependsOn("shadowJar")
  }

  val buildExecutable by
      registering(ReallyExecJar::class) {
        val shadowJar = named("shadowJar", Jar::class) // project.tasks.shadowJar
        jarFile.set(shadowJar.flatMap { it.archiveFile })
        javaOpts.set(application.applicationDefaultJvmArgs)
        onlyIf { OperatingSystem.current().isUnix }
      }

  val copyTemplates by
      registering(Copy::class) {
        description = "Generate template classes"
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        // GitHub actions workaround
        val props = project.properties.toMutableMap()
        props["git_branch"] = project.findProperty("branch_name")
        props["git_tag"] = project.findProperty("base_tag")

        // Find resolved runtime dependencies
        val dependencies =
            project.configurations
                .named("runtimeClasspath")
                .get()
                .resolvedConfiguration
                .resolvedArtifacts
                .map { it.moduleVersion.id.toString() }
                .sorted()
                .joinToString(System.getProperty("line.separator"))
        props["dependencies"] = dependencies

        if (debugEnabled) {
          props.forEach { (t, u) -> println("%1\$-42s --> %2\$s".format(t, u)) }
        }

        filteringCharset = "UTF-8"
        from(project.projectDir.resolve("src/main/templates"))
        into(project.buildDir.resolve("generated-sources/templates/kotlin/main"))
        exclude { it.name.startsWith("jte") }
        expand(props)

        // val configuredVersion =
        // providers.gradleProperty("version").forUseAtConfigurationTime().get()
        // expand("configuredVersion" to configuredVersion)
        // inputs.property("buildversions", props.hashCode())
      }

  // jdeprscan task configuration
  val jdepExtn = extensions.create<JdeprscanExtension>("jdeprscan")
  val jdeprscan = register<Jdeprscan>("jdeprscan", jdepExtn)
  jdeprscan {
    val shadowJar by existing(Jar::class)
    jarFile.set(shadowJar.flatMap { it.archiveFile })
  }

  // Dev task that does both the continuous compiling and run.
  register("dev") {
    description = "A task to continuous compile and run"
    group = LifecycleBasePlugin.BUILD_GROUP

    doLast {
      val continuousCompile = forkTask("classes", "-t")
      val run = forkTask("run")
      CompletableFuture.allOf(continuousCompile, run).join()
    }
  }

  // Add the generated templates to the source set.
  sourceSets { main { java.srcDirs(copyTemplates) } }

  build { finalizedBy(printModuleDeps, buildExecutable, githubActionOutput) }

  val ciBuild by registering {
    description = "Custom task for GitHub action CI build"
    dependsOn(tasks.run, tasks.build, "koverMergedHtmlReport", "dokkaHtml")
    named("koverMergedHtmlReport").map { it.mustRunAfter(tasks.build) }
    named("dokkaHtml").map { it.mustRunAfter(tasks.build) }
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
//    javaLauncher.set(project.javaToolchains.launcherFor(java.toolchain))
//  }

// Gradle dependency substitution
// ------------------------------
// allprojects {
//    configurations.all {
//        resolutionStrategy.dependencySubstitution {
//            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
//                using(module("androidx.compose.compiler:compiler:1.2.0"))
//                using(project(":api"))
//            }
//        }
//    }
// }
