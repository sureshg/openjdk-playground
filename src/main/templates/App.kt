/**
 * Returns the app's runtime version. Releases follow [semantic versioning][semver]. Versions with
 * the `-SNAPSHOT` qualifier are not unique and should only be used in development environments.
 *
 * Note that app's runtime version may be different from the version specified in your project's
 * build file due to the dependency resolution features of your build tool.
 *
 * [semver]: https://semver.org
 */
object App {
  /** Application version. */
  const val VERSION = "$version"

  /** Application base version used for calculating the version. */
  const val BASE_VERSION = "$base_version"

  /** Java version used for building the app. */
  const val JAVA_VERSION = "${getProperty('systemProp.javaVersion')}"

  /** Kotlin library version. */
  const val KOTLIN_VERSION = "${getProperty('systemProp.kotlinVersion')}"

  /** Gradle version */
  const val GRADLE_VERSION = "${getProperty('systemProp.gradleRelease')}"

  /**
   * Git
   * [Metadata](https://github.com/jgitver/jgitver/blob/master/src/main/java/fr/brouillard/oss/jgitver/metadata/Metadatas.java)
   * .
   */
  const val GIT_SHA1_8 = "$git_sha1_8"

  const val GIT_SHA1_FULL = "$git_sha1_full"

  const val GIT_BRANCH = "$git_branch"

  const val GIT_TAG = "$git_tag"

  const val GIT_COMMIT_TIMESTAMP = "$commit_timestamp"

  const val GIT_COMMITER_NAME = "$head_committer_name"

  const val GIT_COMMITER_EMAIL = "$head_commiter_email"

  /** Returns the list of runtime dependencies. */
  val dependencies: List<String> by lazy {
    """$dependencies""".trimIndent().lines().filter { it.isNotBlank() }
  }
}
