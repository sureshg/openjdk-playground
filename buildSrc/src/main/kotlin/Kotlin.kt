import org.gradle.api.*

internal val Project.isKotlinMPP get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

internal val Project.isKotlinJsProject get() = plugins.hasPlugin("org.jetbrains.kotlin.js")
