package dev.suresh.gradle

import org.gradle.api.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.jvm.toolchain.*
import org.gradle.kotlin.dsl.kotlin
import org.gradle.plugin.use.PluginDependenciesSpec
import org.jetbrains.kotlin.gradle.dsl.*

/** Java version properties. */
val Project.javaVersion
  get() = libs.versions.java.asProvider().map { JavaVersion.toVersion(it) }

val Project.javaRelease
  get() = javaVersion.map { it.majorVersion.toInt() }

val Project.toolchainVersion
  get() = javaVersion.map { JavaLanguageVersion.of(it.majorVersion) }

val Project.toolchainVendor
  get() = libs.versions.java.vendor.map { JvmVendorSpec.matching(it) }

val Project.jvmArguments
  get() = libs.versions.java.jvmArguments.get().split(",", " ").filter { it.isNotBlank() }

val Project.addModules
  get() = libs.versions.java.addModules.get()

/** Kotlin version properties. */
val Project.kotlinVersion
  get() = libs.versions.kotlin.asProvider()

val Project.kotlinJvmTarget
  get() = libs.versions.kotlin.jvmtarget.map { JvmTarget.fromTarget(it) }

val Project.kotlinApiVersion
  get() = libs.versions.kotlin.api.version.map { KotlinVersion.fromVersion(it) }

val Project.kotlinLangVersion
  get() = libs.versions.kotlin.lang.version.map { KotlinVersion.fromVersion(it) }

/** Dependency Extensions */
val DependencyHandler.KotlinBom
  get() = kotlin("bom")

val PluginDependenciesSpec.kotlinAllOpen
  get() = kotlin("plugin.allopen")

val PluginDependenciesSpec.kotlinNoArg
  get() = kotlin("plugin.noarg")

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
