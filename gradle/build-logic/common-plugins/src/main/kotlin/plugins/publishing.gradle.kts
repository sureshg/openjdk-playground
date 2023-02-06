package plugins

import dev.suresh.gradle.libs

plugins {
  signing
  `maven-publish`
}

// Dokka html doc
val dokkaHtmlJar by
    tasks.registering(Jar::class) {
      from(tasks.named("dokkaHtml"))
      archiveClassifier = "htmldoc"
    }

// For publishing a pure kotlin project
val emptyJar by
    tasks.registering(Jar::class) {
      archiveClassifier = "javadoc"
      // duplicatesStrategy = DuplicatesStrategy.EXCLUDE
      // manifest {
      //   attributes("Automatic-Module-Name" to appMainModule)
      // }
    }

publishing {
  repositories {
    maven {
      name = "local"
      url = uri(layout.buildDirectory.dir("repo"))
    }

    maven {
      name = "GitHubPackages"
      url = uri("https://maven.pkg.github.com/sureshg/${project.name}")
      credentials {
        username = project.findProperty("gpr.user") as? String ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as? String ?: System.getenv("TOKEN")
      }
    }
  }

  publications {
    // Maven Central
    register<MavenPublication>("maven") {
      from(components["java"])
      artifact(dokkaHtmlJar)
      artifact(tasks.named("buildExecutable"))
      // artifact(tasks.shadowJar)
      configurePom()
    }

    // GitHub Package Registry
    register<MavenPublication>("gpr") {
      from(components["java"])
      configurePom()
    }
  }
}

fun MavenPublication.configurePom() {
  val githubUrl = libs.versions.githubProject.getOrElse("")
  pom {
    packaging = "jar"
    name = project.name
    description = project.description
    inceptionYear = "2021"
    url = githubUrl

    developers {
      developer {
        id = "sureshg"
        name = "Suresh"
        email = "email@suresh.dev"
        organization = "Suresh"
        organizationUrl = "https://suresh.dev"
      }
    }

    licenses {
      license {
        name = "The Apache Software License, Version 2.0"
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        distribution = "repo"
      }
    }

    scm {
      url = githubUrl
      tag = "HEAD"
      connection = "scm:git:$githubUrl.git"
      developerConnection = "scm:git:$githubUrl.git"
    }

    issueManagement {
      system = "github"
      url = "$githubUrl/issues"
    }
  }
}
