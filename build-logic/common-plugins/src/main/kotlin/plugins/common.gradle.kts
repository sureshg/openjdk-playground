package plugins

import GithubAction
import appRunCmd
import debugEnabled
import forkTask
import mebiSize
import org.gradle.internal.os.OperatingSystem
import tasks.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.*
import java.util.spi.*

plugins {
    idea
    java
    application
    `test-suite-base`
    //`java-library`
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

if (gradle.startParameter.taskNames.any { it == "clean" }) {
    logger.warn(
        """
        CLEANING ALMOST NEVER FIXES YOUR BUILD!
        Cleaning is often a last-ditch effort to fix perceived build problems that aren't going to actually be fixed by cleaning.
        What cleaning will do though is make your next few builds significantly slower because all the incremental compilation data has to be regenerated, so you're really just making your day worse.
        """.trimIndent()
    )
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

    val buildExecutable by registering(ReallyExecJar::class) {
        val shadowJar = named("shadowJar", Jar::class) //project.tasks.shadowJar
        jarFile.set(shadowJar.flatMap { it.archiveFile })
        javaOpts.set(application.applicationDefaultJvmArgs)
        onlyIf { OperatingSystem.current().isUnix }
    }

    named("build") {
        finalizedBy(printModuleDeps, buildExecutable, githubActionOutput)
    }

    // register<Copy>("copyTemplates"){}
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
