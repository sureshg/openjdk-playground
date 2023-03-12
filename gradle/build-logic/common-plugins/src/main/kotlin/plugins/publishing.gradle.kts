package plugins

import dev.suresh.gradle.libs

plugins {
  `maven-publish`
  signing
  id("com.javiersc.semver")
  id("org.cyclonedx.bom")
}

// Nexus plugin needs to apply to the root project only
if (project == rootProject) {
  apply(plugin = "io.github.gradle-nexus.publish-plugin")
}

group = libs.versions.group.get()

tasks {
  cyclonedxBom {
    includeConfigs = listOf("runtimeClasspath")
    skipConfigs = listOf("compileClasspath", "testCompileClasspath")
    destination = project.layout.buildDirectory.dir("sbom").map { it.asFile }
    outputFormat = "json"
    includeLicenseText = true
  }
}

semver { tagPrefix = "v" }

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
    plugins.withId("java") {
      // Maven and GitHub Package Registry publications
      listOf("maven", "gpr").forEach { name ->
        register<MavenPublication>(name) {
          from(components["java"])
          configurePom()
        }
      }
    }

    // Maven Bom
    plugins.withId("java-platform") {
      register<MavenPublication>("maven") {
        from(components["javaPlatform"])
        configurePom()
      }
    }

    // Gradle version catalog
    plugins.withId("version-catalog") {
      register<MavenPublication>("maven") {
        from(components["versionCatalog"])
        configurePom()
      }
    }

    // Dokka html doc jar
    plugins.withId("org.jetbrains.dokka") {
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
            archiveAppendix = "empty"
            duplicatesStrategy = DuplicatesStrategy.WARN
          }

      withType<MavenPublication>().configureEach {
        // add dokka html jar as an artifact
        artifact(dokkaHtmlJar)
      }
    }

    // Executable jar
    plugins.withId("java") {
      if (project == rootProject) {
        withType<MavenPublication>().configureEach {
          artifact(tasks.named("buildExecutable"))
          // artifact(tasks.shadowJar)
        }
      }
    }
  }
}

// signing {
//  setRequired {
//    gradle.taskGraph.allTasks.any {
//      it.name.startsWith("publish")
//    }
//  }
//
//  publishing.publications.configureEach {
//    sign(this)
//  }
//  useGpgCmd()
// }

fun MavenPublication.configurePom() {
  val githubUrl = libs.versions.githubProject.getOrElse("")
  pom {
    name = provider { "${project.group}:${project.name}" }
    description = provider { project.description }
    inceptionYear = "2021"
    url = githubUrl

    developers {
      developer {
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
      }
    }

    scm {
      url = githubUrl
      connection = "scm:git:$githubUrl.git"
      developerConnection = "scm:git:$githubUrl.git"
    }
  }
}
