import org.gradle.api.*
import org.gradle.internal.os.*
import org.gradle.jvm.toolchain.*
import org.slf4j.*
import java.io.*
import java.nio.file.*
import kotlin.properties.*
import kotlin.reflect.*

/**
 * Build source logger
 */
val logger = LoggerFactory.getLogger("buildSrc")

/**
 * Maven and gradle repositories.
 */
sealed class Repo(val name: String, val url: String) {
  object Central : Repo(
    name = "Maven Central",
    url = "https://repo1.maven.org/maven2/"
  )

  object OssrhSnapshots : Repo(
    name = "Sonatype OSSRH Snapshots",
    url = "https://oss.sonatype.org/content/repositories/snapshots/"
  )

  object OssrhStaging : Repo(
    name = "Sonatype OSSRH Staging",
    url = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
  )

  object OssrhReleases : Repo(
    name = "Sonatype OSSRH Releases",
    url = "https://oss.sonatype.org/content/repositories/releases/"
  )

  object OssrhPublic : Repo(
    name = "Sonatype OSSRH Public",
    url = "https://oss.sonatype.org/content/groups/public/"
  )

  object GradlePlugins : Repo(
    name = "Gradle Plugin",
    url = "https://plugins.gradle.org/m2/"
  )

  object Jetbrains : Repo(
    name = "Jetbrains Dev",
    url = "https://packages.jetbrains.team/maven/p/ui/dev"
  )
}

// OS temp location
val tmp = if (OperatingSystem.current().isWindows) "c:/TEMP" else "/tmp"

// Quote for -Xlog file
val xQuote = if (OperatingSystem.current().isWindows) """\"""" else """""""

/**
 * Check if it's a non stable (RC) version.
 */
val String.isNonStable: Boolean
  get() {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
  }

/**
 * Checks if the project has snapshot version.
 */
fun Project.hasSnapshotVersion() = version.toString().endsWith("SNAPSHOT", true)

/**
 * Returns the JDK install path provided by the [JavaToolchainService]
 */
val Project.javaToolchainPath
  get(): String {
    val javaToolchains = extensions.getByName("javaToolchains") as JavaToolchainService
    return javaToolchains.launcherFor {
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }.orNull
      ?.metadata
      ?.installationPath?.toString()
      ?: error("Requested JDK version ($javaVersion) is not available.")
  }

/**
 * Returns the application `run` command.
 */
fun Project.appRunCmd(jarPath: Path, args: List<String>): String {
  val path = projectDir.toPath().relativize(jarPath)
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

/**
 * Returns the current OS name.
 */
val os by lazy {
  val os = System.getProperty("os.name")
  when {
    os.equals("Mac OS X", ignoreCase = true) -> "macos"
    os.startsWith("Win", ignoreCase = true) -> "windows"
    os.startsWith("Linux", ignoreCase = true) -> "linux"
    else -> error("Unsupported OS: $os")
  }
}

/**
 * System property delegate
 */
@Suppress("IMPLICIT_CAST_TO_ANY")
inline fun <reified T> sysProp(): ReadOnlyProperty<Any?, T> =
  ReadOnlyProperty { _, property ->
    val propVal = System.getProperty(property.name, "")
    val propVals = propVal.split(",", " ").filter { it.isNotBlank() }

    val kType = typeOf<T>()
    when (kType) {
      typeOf<String>() -> propVal
      typeOf<Int>() -> propVal.toInt()
      typeOf<Boolean>() -> propVal.toBoolean()
      typeOf<Long>() -> propVal.toLong()
      typeOf<Double>() -> propVal.toDouble()
      typeOf<List<String>>() -> propVals
      typeOf<List<Int>>() -> propVals.map { it.toInt() }
      typeOf<List<Long>>() -> propVals.map { it.toLong() }
      typeOf<List<Double>>() -> propVals.map { it.toDouble() }
      typeOf<List<Boolean>>() -> propVals.map { it.toBoolean() }
      else -> error("'${property.name}' system property type ($kType) is not supported!")
    } as T
  }

/**
 * Find the file ends with given [format] under the directory.
 */
fun File.findPkg(format: String?) = when (format != null) {
  true -> walk().firstOrNull { it.isFile && it.name.endsWith(format, ignoreCase = true) }
  else -> null
}

val isGithubAction get() = System.getenv("GITHUB_ACTIONS").toBoolean()

/**
 * Add a Github action output if it's running on an Action runner.
 */
fun ghActionOutput(name: String, value: Any) {
  if (isGithubAction) println("::set-output name=$name::$value")
}
