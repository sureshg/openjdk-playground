import org.gradle.api.*
import java.io.*

val Project.isKotlinMPP get() = plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")

val Project.isKotlinJsProject get() = plugins.hasPlugin("org.jetbrains.kotlin.js")

// val debug: String? by project
val Project.debugEnabled get() = properties["debug"]?.toString()?.toBoolean() ?: false

val File.mebiSize get() = "%.2f MiB".format(length() / (1024 * 1024f))