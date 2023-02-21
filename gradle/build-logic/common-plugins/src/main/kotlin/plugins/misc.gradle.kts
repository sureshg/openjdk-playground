package plugins

import com.github.benmanes.gradle.versions.reporter.result.Result
import dev.suresh.gradle.forkTask
import dev.suresh.gradle.libs
import java.util.concurrent.CompletableFuture
import org.gradle.kotlin.dsl.*
import tasks.BuildConfig

plugins {
  java
  wrapper
  // id("common")
  // id("gg.jte.gradle")
  id("com.diffplug.spotless")
  id("com.github.ben-manes.versions")
  id("com.github.johnrengelman.shadow")
  id("io.github.gradle-nexus.publish-plugin")
  id("com.autonomousapps.plugin-best-practices-plugin")
  id("org.jetbrains.dokka")

  // id("com.autonomousapps.dependency-analysis")
  // id("org.openrewrite.rewrite")
  // id("de.benediktritter.maven-plugin-development")
  // id("io.fuchs.gradle.classpath-collision-detector")
  // id("org.barfuin.gradle.taskinfo").apply(false)
}

val ktfmtVersion = libs.versions.ktfmt.get()
val gjfVersion = libs.versions.google.javaformat.get()

// jte {
//   contentType = ContentType.Plain
//   generateNativeImageResources = true
//   generate()
// }

// Formatting
spotless {
  java {
    googleJavaFormat(gjfVersion)
    target("**/*.java")
    targetExclude("**/build/**", "**/.gradle/**")
  }
  // if(plugins.hasPlugin(JavaPlugin::class.java)){ }

  kotlin {
    ktfmt(ktfmtVersion)
    target("**/*.kt")
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("**/build/**", "**/.gradle/**")
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktfmt(ktfmtVersion)
    target("**/*.gradle.kts")
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("**/build/**")
  }

  format("misc") {
    target("**/*.md", "**/.gitignore")
    trimTrailingWhitespace()
    indentWithSpaces(2)
    endWithNewline()
  }
  // isEnforceCheck = false
}

// dependencyAnalysis { issues { all { onAny { severity("warn") } } } }

tasks {
  // Generate build config.
  val buildConfig by registering(BuildConfig::class) { classFqName = "BuildConfig" }
  sourceSets { main { java.srcDirs(buildConfig) } }

  // Fix "Execution optimizations have been disabled" warning for JTE
  // tasks.named("dokkaHtml") { dependsOn(tasks.generateJte) }

  jar { exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA") }

  // Delegating tasks to composite build.
  gradle.includedBuilds.forEach { incBuild ->
    val task = "build"
    val incBuildName = incBuild.name

    val buildTasks =
        incBuild.projectDir
            .listFiles()
            ?.filter { it.isDirectory && File(it, "build.gradle.kts").exists() }
            ?.map { dir -> incBuild.task(":${dir.name}:$task") }
            ?.toTypedArray()
            .orEmpty()

    register("${incBuildName.lowercase()}-$task") {
      description = "Build all projects in $incBuildName"
      group = BasePlugin.BUILD_GROUP
      dependsOn(*buildTasks)
      // dependsOn(gradle.includedBuild("jdk-modules").task(":ffm-api:build"))
    }
  }

  // Clean all composite builds
  register("cleanAll") {
    description = "Clean all composite builds"
    group = LifecycleBasePlugin.CLEAN_TASK_NAME
    gradle.includedBuilds.forEach { dependsOn(it.task(":clean")) }
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

  // Dependency version updates
  dependencyUpdates {
    checkForGradleUpdate = true
    checkConstraints = true
    outputFormatter =
        closureOf<Result> {
          outdated.dependencies.forEach { dep ->
            println("${dep.group}:${dep.name} -> ${dep.available.release}")
          }
        }
    // rejectVersionIf { candidate.version.isNonStable && !currentVersion.isNonStable }

    // Run "dependencyUpdates" on all "build-logic" projects also.
    gradle.includedBuild("build-logic").also { buildLogic ->
      buildLogic.projectDir
          .listFiles()
          ?.filter { it.isDirectory && File(it, "build.gradle.kts").exists() }
          ?.forEach { dir -> dependsOn(buildLogic.task(":${dir.name}:dependencyUpdates")) }
    }
  }

  // Reproducible builds
  withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
  }

  wrapper {
    gradleVersion = libs.versions.gradle.asProvider().get()
    distributionType = Wrapper.DistributionType.ALL
  }

  // Default task (--rerun-tasks --no-build-cache)
  defaultTasks("clean", "tasks", "--all")

  // signing {
  //   setRequired({ signPublications == "true" })
  //   sign(publishing.publications["maven"])
  // }
}
