package plugins

import debugEnabled
import mebiSize
import org.gradle.internal.os.OperatingSystem
import org.gradle.tooling.*
import tasks.*
import java.io.*
import java.nio.file.*
import java.nio.file.Path
import java.util.concurrent.*
import java.util.spi.*

plugins {
    idea
    java
    application
    `test-suite-base`
// `java-library`
}

// apply(from ="")

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
    project.vcs = "Git"
}

if (debugEnabled) {
    // Print all the tasks
    gradle.taskGraph.whenReady {
        allTasks.forEachIndexed { index, task ->
            println("${index + 1}. ${task.name}")
        }
    }
}

// After the project configure
afterEvaluate {
    println("=== Project Configuration Completed ===")
}

tasks {

    // Clean all composite builds
    register("cleanAll") {
        description = "Clean all composite builds"
        group = LifecycleBasePlugin.CLEAN_TASK_NAME

        gradle.includedBuilds.forEach {
            dependsOn(it.task(":clean"))
        }
    }

    jar {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }

    val printModuleDeps by registering {
        description = "Print Java Platform Module dependencies of the application."
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        doLast {
            val uberJar = named("shadowJar", Jar::class)
            val jarFile = uberJar.get().archiveFile.get().asFile

            val jdeps = ToolProvider.findFirst("jdeps")
                .orElseGet { error("jdeps tool is missing in the JDK!") }
            val out = StringWriter()
            val pw = PrintWriter(out)
            jdeps.run(
                pw, pw,
                "-q",
                "-R",
                "--print-module-deps",
                "--ignore-missing-deps",
                "--multi-release=${java.toolchain.languageVersion.get().asInt()}",
                jarFile.absolutePath
            )

            val modules = out.toString()
            println(modules)
        }
        dependsOn("shadowJar")
    }

    val buildExecutable by registering {
        description = "Build executable binary"
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        doLast {
            val uberJar = named("shadowJar", Jar::class) //project.tasks.shadowJar
            val jarFile = uberJar.get().archiveFile.get().asFile
            val binFile = File(project.buildDir, project.name)

            val shell = project.projectDir.resolve("gradle/exec-jar-stub.sh")
                .readText()
                .replace(
                    "${'$'}JAVA_OPTS",
                    application.applicationDefaultJvmArgs.joinToString(" ")
                )

            FileOutputStream(binFile).buffered().use { bos ->
                bos.write(shell.encodeToByteArray())
                Files.copy(jarFile.toPath(), bos)
            }
            binFile.setExecutable(true)
            println("Executable : ${binFile.path} ${binFile.mebiSize}")
        }

        onlyIf { OperatingSystem.current().isUnix }
        dependsOn("shadowJar")
    }

    val githubActionOutput by registering {
        description = "Set Github workflow action output for this build"
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        doLast {
            val uberJar = named("shadowJar", Jar::class).get().archiveFile.get().asFile
            println("Uber Jar : ${uberJar.path} ${uberJar.mebiSize}")
            println(uberJar.toPath().appRunCmd(application.applicationDefaultJvmArgs.toList()))

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

    named("build") {
        finalizedBy(printModuleDeps, buildExecutable, githubActionOutput)
    }

    // register<Copy>("copyTemplates")
    val copyTemplates by registering(Copy::class) {
        description = "Generate template classes"
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        // Github actions workaround
        val props = project.properties.toMutableMap()
        props["git_branch"] = project.findProperty("branch_name")
        props["git_tag"] = project.findProperty("base_tag")

        if (debugEnabled) {
            props.forEach { (t, u) ->
                println("%1\$-42s --> %2\$s".format(t, u))
            }
        }

        filteringCharset = "UTF-8"
        from(project.projectDir.resolve("src/main/templates"))
        into(project.buildDir.resolve("generated-sources/templates/kotlin/main"))
        exclude { it.name.startsWith("jte") }
        expand(props)

        // val configuredVersion = providers.gradleProperty("version").forUseAtConfigurationTime().get()
        // expand("configuredVersion" to configuredVersion)
        // inputs.property("buildversions", props.hashCode())
    }

    /** Dev task that does both the continuous compile and run. */
    val dev by registering {
        description = "A task to continuous compile and run"
        group = LifecycleBasePlugin.BUILD_GROUP

        doLast {
            val continuousCompile = forkTask("classes", "-t")
            val run = forkTask("run")
            CompletableFuture.allOf(continuousCompile, run).join()
        }
    }

    register<SampleTask>("greetings") {
        greeting.set("Hello Gradle Kotlin DSL!")
    }

//  withType<JavaExec>().matching {  }.configureEach {
//    jvmArgs(
//      "--enable-native-access=ALL-UNNAMED",
//      "--add-modules=jdk.incubator.foreign",
//      "--module-path", files(configurations.compileClasspath).asPath,
//      "--add-modules", "ALL-MODULE-PATH"
//    )
//    javaLauncher.set(project.javaToolchains.launcherFor(java.toolchain))
//  }
}

/** Returns the application `run` command. */
fun Path.appRunCmd(args: List<String>): String {
    val path = projectDir.toPath().relativize(this)
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

/** Fork a new gradle [task] with given [args] */
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
