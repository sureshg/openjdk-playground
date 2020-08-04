package dev.suresh.mvn

import org.jboss.shrinkwrap.resolver.api.maven.*

/**
 * - https://github.com/apache/maven-resolver/tree/master/maven-resolver-demos/maven-resolver-demo-snippets
 * - https://github.com/apache/maven/tree/master/maven-resolver-provider
 * - https://github.com/shrinkwrap/resolver
 * - https://blog.sonatype.com/2011/06/you-dont-need-a-browser-to-use-maven-central/
 * - Dep - implementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.3")
 */
class MavenResolver {
    fun run() {
        // val repo = RemoteRepository.Builder( "central", "default", "https://repo1.maven.org/maven2/" ).build()
        val files = Maven.configureResolver()
            .withMavenCentralRepo(true)
            .resolve("org.jetbrains.kotlin:commonizer:1.3.71")
            .withTransitivity()
            .asResolvedArtifact()

        println("\nResolved Artifacts are,")
        files.forEach {
            println(it.coordinate)
        }
    }
}