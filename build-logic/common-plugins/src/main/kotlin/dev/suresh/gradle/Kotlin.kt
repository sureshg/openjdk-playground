package dev.suresh.gradle

import org.gradle.api.*


val Project.isKotlinMPP get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

val Project.isKotlinJvmProject get() = plugins.hasPlugin("org.jetbrains.kotlin.jvm")

val Project.isKotlinJsProject get() = plugins.hasPlugin("org.jetbrains.kotlin.js")

fun Project.kotlinxHtml(name: String): String =
    kotlinx("kotlinx-html", name, libs.versions.kotlinx.html.get())

fun Project.kotlinxSerialization(name: String): String =
    kotlinx("kotlinx-serialization", name, libs.versions.kotlinx.serialization.asProvider().get())

fun Project.kotlinxCoroutines(name: String): String =
    kotlinx("kotlinx-coroutines", name, libs.versions.kotlinx.coroutines.get())

fun Project.kotlinw(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"

private fun Project.kotlinx(projectName: String, name: String, version: String): String =
    "org.jetbrains.kotlinx:$projectName-$name:$version"

