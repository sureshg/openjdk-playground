package dev.suresh.gradle

import org.gradle.api.*
import org.gradle.api.publish.maven.*

internal val githubProject by sysProp<String>()

fun MavenPublication.configurePom(
    project: Project,
) {
    pom {
        packaging = "jar"
        name.set(project.name)
        description.set(project.description)
        inceptionYear.set("2021")
        url.set(githubProject)

        developers {
            developer {
                id.set("sureshg")
                name.set("Suresh")
                email.set("email@suresh.dev")
                organization.set("Suresh")
                organizationUrl.set("https://suresh.dev")
            }
        }

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        scm {
            url.set(githubProject)
            tag.set("HEAD")
            connection.set("scm:git:$githubProject.git")
            developerConnection.set("scm:git:$githubProject.git")
        }

        issueManagement {
            system.set("github")
            url.set("$githubProject/issues")
        }
    }
}