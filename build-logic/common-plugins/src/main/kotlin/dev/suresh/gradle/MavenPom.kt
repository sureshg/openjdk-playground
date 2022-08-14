package dev.suresh.gradle

import org.gradle.api.*
import org.gradle.api.publish.maven.*

fun MavenPublication.configurePom(project: Project) {
  val githubUrl = project.libs.versions.githubProject.getOrElse("")
  pom {
    packaging = "jar"
    name.set(project.name)
    description.set(project.description)
    inceptionYear.set("2021")
    url.set(githubUrl)

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
      url.set(githubUrl)
      tag.set("HEAD")
      connection.set("scm:git:$githubUrl.git")
      developerConnection.set("scm:git:$githubUrl.git")
    }

    issueManagement {
      system.set("github")
      url.set("$githubUrl/issues")
    }
  }
}
