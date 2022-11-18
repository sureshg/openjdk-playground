/**
 * Application build config template. The gradle system property can be accessed using the
 * **"💲{getProperty('systemProp.xxxx')}"** expression.
 */
object App {
  /** Application version. */
  const val VERSION = "$version"

  /** Application base version used for calculating the version. */
  const val BASE_VERSION = "$base_version"

  /** Java version used for building the app. */
  const val JAVA_VERSION = "$javaVersion"

  /** Kotlin library version. */
  const val KOTLIN_VERSION = "$kotlinVersion"

  /** Gradle version */
  const val GRADLE_VERSION = "$gradleVersion"

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
