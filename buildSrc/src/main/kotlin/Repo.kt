import org.gradle.api.*

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
fun Project.hasSnapshotVersion() = toString().endsWith("SNAPSHOT", true)
