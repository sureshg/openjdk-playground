import org.gradle.api.*

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


