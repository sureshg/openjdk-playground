package plugins

import org.gradle.jvm.tasks.Jar
import org.gradle.tooling.*
import java.io.*
import java.nio.file.*
import java.util.concurrent.*
import java.util.spi.*

plugins {
    java
}

tasks {

    val printModuleDeps by registering {
        description = "Print Java Platform Module dependencies of the application."
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        doLast {
            val uberJar = named("shadowJar", Jar::class)
            val jarFile = uberJar.get().archiveFile.get().asFile

            val jdeps = ToolProvider.findFirst("jdeps").orElseGet { error("") }
            val out = StringWriter()
            val pw = PrintWriter(out)
            jdeps.run(pw, pw, "--print-module-deps", "--ignore-missing-deps", jarFile.absolutePath)

            val modules = out.toString()
            println(modules)
        }
        dependsOn("shadowJar")
    }

    val copyTemplates by registering(Copy::class) {
        description = "Generate template classes."
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        val configuredVersion = providers
            .gradleProperty("version")
            .forUseAtConfigurationTime()
            .get()

        val props = project.extra.properties
        props["projectVersion"] = project.version
        props["configuredVersion"] = configuredVersion
        props["kotlinVersion"] = System.getProperty("kotlinVersion")

        val debug: String by project
        if (debug.toBoolean()) {
            props.forEach { (t, u) ->
                println("%1\$-42s --> %2\$s".format(t, u))
            }
        }

        filteringCharset = "UTF-8"
        inputs.property("buildversions", props.hashCode())
        from(project.projectDir.resolve(Paths.get("src", "main", "templates").toFile()))
        into(
            project.buildDir.resolve(
                Paths.get("generated-sources", "templates", "kotlin", "main").toFile()
            )
        )
        exclude { it.name.startsWith("jte") }
        expand(props)
    }

    /**
     * Dev task that does both the continuous compile and run.
     */
    val dev by registering {
        description = "A task to continuous compile and run"
        group = LifecycleBasePlugin.BUILD_GROUP

        doLast {
            val continuousCompile = forkTask("classes", "-t")
            val run = forkTask("run")
            CompletableFuture.allOf(continuousCompile, run).join()
        }
    }
}

/**
 * Fork a new gradle [task] with given [args]
 */
fun forkTask(task: String, vararg args: String) = CompletableFuture.supplyAsync {
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
