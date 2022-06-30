package dev.suresh.gradle

import org.gradle.accessors.dm.*
import org.gradle.api.*
import org.gradle.api.artifacts.*
import org.gradle.api.attributes.*
import org.gradle.api.component.*
import org.gradle.api.plugins.*
import org.gradle.internal.os.*
import org.gradle.jvm.toolchain.*
import org.gradle.kotlin.dsl.*
import org.gradle.tooling.*
import java.io.*
import java.nio.file.*
import java.util.concurrent.*

internal val Project.libs get() = the<LibrariesForLibs>()

/** Quote for -Xlog file */
val Project.xQuote get() = if (OperatingSystem.current().isWindows) """\"""" else """""""

val Project.isPlatformProject get() = plugins.hasPlugin("java-platform")

val Project.isJavaLibraryProject get() = plugins.hasPlugin("java-library")

// val debug: String? by project
val Project.debugEnabled get() = properties["debug"]?.toString()?.toBoolean() ?: false

val Project.hasCleanTask get() = gradle.startParameter.taskNames.any { it == "clean" }

/** Checks if the project has a snapshot version. */
val Project.isSnapshot get() = version.toString().endsWith("SNAPSHOT", true)

/** Check if it's a non-stable(RC) version. */
val String.isNonStable: Boolean
    get() {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
        val regex = "^[\\d,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(this)
        return isStable.not()
    }

/**
 * Returns the JDK install path provided by the [JavaToolchainService]
 */
val Project.javaToolchainPath
    get(): Path {
        val defToolchain = extensions.findByType(JavaPluginExtension::class)?.toolchain
        val javaToolchainSvc = extensions.findByType(JavaToolchainService::class)
        val javaVersion = libs.versions.java.asProvider().get()

        val jLauncher = when (defToolchain != null) {
            true -> javaToolchainSvc?.launcherFor(defToolchain)
            else -> javaToolchainSvc?.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }
        }?.orNull

        return jLauncher
            ?.metadata
            ?.installationPath
            ?.asFile
            ?.toPath()
            ?: error("Requested JDK version ($javaVersion) is not available.")
    }

/** Print all the catalog version strings and it's values. */
fun Project.printVersionCatalog() {
    if (debugEnabled) {
        val catalogs = the<VersionCatalogsExtension>()
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
            allTasks.forEachIndexed { index, task ->
                println("${index + 1}. ${task.name}")
            }
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
        prefix = """
        To Run the app,
        ${'$'} java -jar $lineCont $newLine
        """.trimIndent(),
        postfix = "$newLine$indent$path",
        separator = newLine,
    ) {
        // Escape the globstar
        "$indent$it $lineCont"
            .replace("*", """\*""")
            .replace(""""""", """\"""")
    }
}

/** Adds [file] as an outgoing variant to publication. */
fun Project.addFileToJavaComponent(file: File) {
    // Here's a configuration to declare the outgoing variant
    val executable by configurations.creating {
        description = "Declares executable outgoing variant"
        isCanBeConsumed = true
        isCanBeResolved = false
        attributes {
            // See https://docs.gradle.org/current/userguide/variant_attributes.html
            attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.objects.named("exe"))
        }
    }

    // val binFile = layout.buildDirectory.file("file")
    // val binArtifact = artifacts.add("archives", binFile.get().asFile) {
    //    type = "bin"
    //    builtBy("gradle")
    // }
    // Add it to "artifact(binArtifact)" in "publications"

    executable.outgoing.artifact(file) {
        classifier = "bin"
    }

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
        GradleConnector.newConnector()
            .forProjectDirectory(project.projectDir)
            .connect()
            .use {
                it.newBuild()
                    .addArguments(*args)
                    .addJvmArguments(
                        "--add-opens",
                        "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED"
                    )
                    .forTasks(task)
                    .setStandardError(System.err)
                    .setStandardInput(System.`in`)
                    .setStandardOutput(System.out)
                    .run()
            }
    }
