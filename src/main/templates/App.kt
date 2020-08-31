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
    const val VERSION = "$projectVersion"
}
