package plugins

import org.gradle.tooling.*
import java.io.*
import java.util.concurrent.*
import java.util.spi.*

plugins {
  idea
  java
  application
  `test-suite-base`
// `java-library`
//  id("kotlinx-kover") //version "0.2.2"
}

// apply(from ="")

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
  project.vcs = "Git"
}

// Print all the tasks
gradle.taskGraph.whenReady {
  allTasks.forEachIndexed { index, task ->
    println("${index + 1}. ${task.name}")
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

      val jdeps =
        ToolProvider.findFirst("jdeps").orElseGet { error("jdeps tool is missing in the JDK!") }
      val out = StringWriter()
      val pw = PrintWriter(out)
      jdeps.run(pw, pw, "--print-module-deps", "--ignore-missing-deps", jarFile.absolutePath)

      val modules = out.toString()
      println(modules)
    }

    dependsOn("shadowJar")
  }

  val copyTemplates by registering(Copy::class) {
    description = "Generate template classes"
    group = LifecycleBasePlugin.BUILD_TASK_NAME

    // Github actions workaround
    val props = project.properties.toMutableMap()
    props["git_branch"] = project.findProperty("branch_name")
    props["git_tag"] = project.findProperty("base_tag")

    val debug: String? by project
    if (debug.toBoolean()) {
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

//  withType<JavaExec>().configureEach {
//    jvmArgs(
//      "--enable-native-access=ALL-UNNAMED",
//      "--add-modules=jdk.incubator.foreign",
//      "--module-path", files(configurations.compileClasspath).asPath,
//      "--add-modules", "ALL-MODULE-PATH"
//    )
//    javaLauncher.set(project.javaToolchains.launcherFor(java.toolchain))
//  }
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
