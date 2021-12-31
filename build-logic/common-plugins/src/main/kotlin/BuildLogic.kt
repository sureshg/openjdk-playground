import org.gradle.api.*

val Project.isKotlinMPP get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

val Project.isKotlinJsProject get() = plugins.hasPlugin("org.jetbrains.kotlin.js")
