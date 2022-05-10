import org.gradle.api.*
import org.gradle.api.attributes.*
import org.gradle.api.component.*
import org.gradle.kotlin.dsl.*
import org.gradle.tooling.*
import java.io.*
import java.util.concurrent.*

val File.mebiSize get() = "%.2f MiB".format(length() / (1024 * 1024f))

val Project.isKotlinMPP get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

val Project.isKotlinJsProject get() = plugins.hasPlugin("org.jetbrains.kotlin.js")

// val debug: String? by project
val Project.debugEnabled get() = properties["debug"]?.toString()?.toBoolean() ?: false

/** Returns the application `run` command. */
fun Project.appRunCmd(jar: File, args: List<String>): String {
    val path = layout.projectDirectory.asFile.relativeTo(jar)
    val newLine = System.lineSeparator()
    val lineCont = """\""" // Bash line continuation
    val indent = "\t"
    println()
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
