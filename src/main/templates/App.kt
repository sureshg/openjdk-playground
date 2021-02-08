/**
 * Returns the app's runtime version. Releases follow [semantic versioning][semver].
 * Versions with the `-SNAPSHOT` qualifier are not unique and should only be used
 * in development environments.
 *
 * Note that app's runtime version may be different from the version specified in your
 * project's build file due to the dependency resolution features of your build tool.
 *
 * [semver]: https://semver.org
 */
object App {
    /**
     * Application version.
     */
    const val VERSION = "$projectVersion"

    /**
     * Kotlin library version.
     */
    const val KOTLIN_VERSION = "$kotlinVersion"

    /**
     * Git [Metadata](https://github.com/jgitver/jgitver/blob/master/src/main/java/fr/brouillard/oss/jgitver/metadata/Metadatas.java).
     */
    const val GIT_SHA1_8 = "$git_sha1_8"
}
