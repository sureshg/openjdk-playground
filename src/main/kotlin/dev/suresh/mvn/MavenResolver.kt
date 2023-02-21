package dev.suresh.mvn

import com.squareup.tools.maven.resolution.Artifact
import com.squareup.tools.maven.resolution.ArtifactResolver

/**
 * [maven-resolver-demo-snippets](https://github.com/apache/maven-resolver/tree/master/maven-resolver-demos/maven-resolver-demo-snippets)
 *
 * [maven-resolver-provider](https://github.com/apache/maven/tree/master/maven-resolver-provider)
 *
 * [shrinkwrap](https://github.com/shrinkwrap/resolver)
 *
 * [MavenCentral-SolrSearch](https://blog.sonatype.com/2011/06/you-dont-need-a-browser-to-use-maven-central/)
 */
class MavenResolver {

  private val resolver = ArtifactResolver()

  fun run() {
    val artifacts = resolveTransitively("org.jetbrains.kotlin:kotlin-stdlib:${App.KOTLIN_VERSION}")
    println("\nResolved artifacts are,")
    artifacts.forEach { println(it) }
  }

  private fun resolveTransitively(spec: String): Set<String> {
    val deps = mutableSetOf<String>()
    val resolve =
        DeepRecursiveFunction<Artifact, Set<String>> {
          if (it.coordinate !in deps) {
            deps.add(it.coordinate)
            val artifact = resolver.artifactFor(it.coordinate)
            resolver.resolve(artifact).artifact?.model?.dependencies?.forEach { dep ->
              val depSpec = "${dep.groupId}:${dep.artifactId}:${dep.version}"
              if (depSpec !in deps && dep.scope != "test") {
                deps.addAll(callRecursive(resolver.artifactFor(depSpec)))
              }
            }
          }
          deps
        }
    return resolve(resolver.artifactFor(spec))
  }
}
