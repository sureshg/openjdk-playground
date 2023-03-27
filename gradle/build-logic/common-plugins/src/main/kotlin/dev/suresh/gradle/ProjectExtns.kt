package dev.suresh.gradle

import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.attributes.*
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.*
import org.gradle.tooling.GradleConnector

/** Returns version catalog of this project. */
internal val Project.libs
  get() = the<LibrariesForLibs>()

/**
 * Returns version catalog extension of this project. Give access to all version catalogs available.
 */
internal val Project.catalogs
  get() = the<VersionCatalogsExtension>()

// val logger = LoggerFactory.getLogger("build-logic")

/** Quote for -Xlog file */
val Project.xQuote
  get() = if (OperatingSystem.current().isWindows) """\"""" else """""""

val Project.isPlatformProject
  get() = plugins.hasPlugin("java-platform")

val Project.isJavaLibraryProject
  get() = plugins.hasPlugin("java-library")

// val debug: String? by project
val Project.debugEnabled
  get() = properties["debug"]?.toString().toBoolean()

val Project.hasCleanTask
  get() = gradle.startParameter.taskNames.any { it == "clean" }

/** Checks if the project has a snapshot version. */
val Project.isSnapshot
  get() = version.toString().endsWith("SNAPSHOT", true)

val Project.runsOnCI
  get() = providers.environmentVariable("CI").getOrElse("false").toBoolean()

/** Check if it's a non-stable(RC) version. */
val String.isNonStable: Boolean
  get() {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[\\d,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
  }

/** Returns the JDK install path provided by the [JavaToolchainService] */
val Project.javaToolchainPath
  get(): Path {
    val defToolchain = extensions.findByType(JavaPluginExtension::class)?.toolchain
    val javaToolchainSvc = extensions.findByType(JavaToolchainService::class)

    val jLauncher =
        when (defToolchain != null) {
          true -> javaToolchainSvc?.launcherFor(defToolchain)
          else -> javaToolchainSvc?.launcherFor { languageVersion = toolchainVersion }
        }?.orNull

    return jLauncher?.metadata?.installationPath?.asFile?.toPath()
        ?: error("Requested JDK version ($javaVersion) is not available.")
  }

/** Return incubator modules of the tool chain JDK */
val Project.incubatorModules
  get(): String {
    val javaCmd = project.javaToolchainPath.resolve("bin").resolve("java")
    val bos = ByteArrayOutputStream()
    val execResult = exec {
      workingDir = layout.buildDirectory.get().asFile
      commandLine = listOf(javaCmd.toString())
      args = listOf("--list-modules")
      standardOutput = bos
      errorOutput = bos
    }
    execResult.assertNormalExitValue()
    return bos.toString(Charsets.UTF_8)
        .lines()
        .filter { it.startsWith("jdk.incubator") }
        .joinToString(",") { it.substringBefore("@").trim() }
  }

/** Returns the path of the dependency jar in runtime classpath. */
fun Project.dependencyPath(dep: ExternalDependency) =
    configurations
        .named("runtimeClasspath")
        .get()
        .resolvedConfiguration
        .resolvedArtifacts
        .find { it.moduleVersion.id.module == dep.module }
        ?.file
        ?.path
        ?: error("Could not find ${dep.name} in runtime classpath")

/**
 * Print all the catalog version strings and it's values.
 *
 * [VersionCatalogsExtension](https://docs.gradle.org/current/userguide/platforms.html#sub:type-unsafe-access-to-catalog)
 */
fun Project.printVersionCatalog() {
  if (debugEnabled) {
    catalogs.catalogNames.map { cat ->
      println("=== Catalog $cat ===")
      val catalog = catalogs.named(cat)
      catalog.versionAliases.forEach { alias ->
        println("${alias.padEnd(30, '-')}-> ${catalog.findVersion(alias).get()}")
      }
    }
  }
}

/** Print all the tasks */
fun Project.printTaskGraph() {
  if (debugEnabled) {
    gradle.taskGraph.whenReady {
      allTasks.forEachIndexed { index, task -> println("${index + 1}. ${task.name}") }
    }
  }
}

/** Returns the application `run` command. */
fun Project.appRunCmd(jar: File, args: List<String>): String {
  val path = layout.projectDirectory.asFile.toPath().relativize(jar.toPath())
  val newLine = System.lineSeparator()
  val lineCont = """\""" // Bash line continuation
  val indent = "\t"
  return args.joinToString(
      prefix =
          """
             |To Run the app,
             |${'$'} java -jar $lineCont $newLine
             """
              .trimMargin(),
      postfix = "$newLine$indent$path",
      separator = newLine,
  ) {
    // Escape the globstar
    "$indent$it $lineCont".replace("*", """\*""")
  }
}

/** Adds [file] as an outgoing variant to publication. */
fun Project.addFileToJavaComponent(file: File) {
  // Here's a configuration to declare the outgoing variant
  val executable by
      configurations.creating {
        description = "Declares executable outgoing variant"
        isCanBeConsumed = true
        isCanBeResolved = false
        attributes {
          // See https://docs.gradle.org/current/userguide/variant_attributes.html
          attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
          attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("exe"))
        }
      }

  // val binFile = layout.buildDirectory.file("file")
  // val binArtifact = artifacts.add("archives", binFile.get().asFile) {
  //    type = "bin"
  //    builtBy("gradle")
  // }
  // Add it to "artifact(binArtifact)" in "publications"

  executable.outgoing.artifact(file) { classifier = "bin" }

  val javaComponent = project.components.findByName("java") as AdhocComponentWithVariants
  javaComponent.addVariantsFromConfiguration(executable) {
    // dependencies for this variant are considered runtime dependencies
    mapToMavenScope("runtime")
    // and also optional dependencies, because we don't want them to leak
    mapToOptional()
  }
}

/** Fork a new gradle [task] with given [args] */
fun Project.forkTask(task: String, vararg args: String): CompletableFuture<Unit> =
    CompletableFuture.supplyAsync {
      GradleConnector.newConnector().forProjectDirectory(project.projectDir).connect().use {
        it.newBuild()
            .addArguments(*args)
            .addJvmArguments(
                "--add-opens",
                "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            )
            .forTasks(task)
            .setStandardError(System.err)
            .setStandardInput(System.`in`)
            .setStandardOutput(System.out)
            .run()
      }
    }

/** Lazy version of [TaskContainer.maybeCreate] */
fun <T : Task> TaskContainer.maybeRegister(
    taskName: String,
    type: Class<T>,
    configAction: T.() -> Unit
) =
    when (taskName) {
      in names -> named(taskName, type)
      else -> register(taskName, type)
    }.also { it.configure(configAction) }

fun TaskContainer.maybeRegister(taskName: String, configAction: Task.() -> Unit) =
    maybeRegister(taskName, Task::class.java, configAction)
