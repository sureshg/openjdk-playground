package dev.suresh.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.kotlin

/** Java/Kotlin versions. */
val Project.javaVersion
  get() = libs.versions.java.asProvider().map { it.toInt() }
val Project.kotlinVersion
  get() = libs.versions.kotlin.asProvider()
val Project.kotlinJvmTarget
  get() = libs.versions.kotlin.jvm.target.get()
val Project.kotlinApiVersion
  get() = libs.versions.kotlin.api.version.get()
val Project.kotlinLangVersion
  get() = libs.versions.kotlin.lang.version.get()
val Project.jvmArguments
  get() = libs.versions.java.jvmArguments.get().split(",", " ").filter { it.isNotBlank() }
val Project.addModules
  get() = libs.versions.java.addModules.get()

/** Dependency Extensions */
val DependencyHandler.KotlinBom
  get() = kotlin("bom")
val DependencyHandler.KotlinStdlibJdk8
  get() = kotlin("stdlib-jdk8")
val DependencyHandler.KotlinReflect
  get() = kotlin("reflect")
val DependencyHandler.KotlinTest
  get() = kotlin("test")
val DependencyHandler.KotlinTestJunit
  get() = kotlin("test-junit")
val DependencyHandler.KotlinScriptRuntime
  get() = kotlin("script-runtime")
val DependencyHandler.KotlinScripUtil
  get() = kotlin("script-util")
val DependencyHandler.KotlinScripJsr223
  get() = kotlin("scripting-jsr223")

/** Kotlin Dependencies extension functions. */
val Project.isKotlinMPP
  get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

val Project.isKotlinJvmProject
  get() = plugins.hasPlugin("org.jetbrains.kotlin.jvm")

val Project.isKotlinJsProject
  get() = plugins.hasPlugin("org.jetbrains.kotlin.js")

fun Project.kotlinxHtml(name: String): String =
    kotlinx("kotlinx-html", name, libs.versions.kotlinx.html.get())

fun Project.kotlinxSerialization(name: String): String =
    kotlinx("kotlinx-serialization", name, libs.versions.kotlinx.serialization.asProvider().get())

fun Project.kotlinxCoroutines(name: String): String =
    kotlinx("kotlinx-coroutines", name, libs.versions.kotlinx.coroutines.get())

fun Project.kotlinw(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"

private fun Project.kotlinx(projectName: String, name: String, version: String): String =
    "org.jetbrains.kotlinx:$projectName-$name:$version"
