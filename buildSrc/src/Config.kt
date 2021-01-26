import org.gradle.api.*
import org.gradle.jvm.toolchain.*
import org.slf4j.*
import kotlin.properties.*

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

    object KotlinEAP : Repo(
        name = "Kotlin EAP",
        url = "https://dl.bintray.com/kotlin/kotlin-eap"
    )

    object KotlinDev : Repo(
        name = "Kotlin Dev",
        url = "https://dl.bintray.com/kotlin/kotlin-dev"
    )

    object Jetbrains : Repo(
        name = "Jetbrains Dev",
        url = "https://packages.jetbrains.team/maven/p/ui/dev"
    )
}

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
 * Returns the current OS name.
 */
val os by lazy {
    val os = System.getProperty("os.name")
    when {
        os == "Mac OS X" -> "macos"
        os.startsWith("Win") -> "windows"
        os.startsWith("Linux") -> "linux"
        else -> error("Unsupported OS: $os")
    }
}

/**
 * System property delegate
 */
inline fun <reified T> sysProp(): ReadOnlyProperty<Any?, T> =
    ReadOnlyProperty { _, property ->
        val propVal: String = System.getProperty(property.name, "")
        logger.info("Getting System Property '${property.name}': $propVal")
        when (T::class) {
            String::class -> propVal
            Int::class -> propVal.toInt()
            Boolean::class -> propVal.toBoolean()
            Long::class -> propVal.toLong()
            else -> error("${T::class.simpleName} type system property is not supported!")
        } as T
    }
