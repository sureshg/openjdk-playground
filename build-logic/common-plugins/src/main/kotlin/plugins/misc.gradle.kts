package plugins

import dev.suresh.gradle.libs
import tasks.BuildConfig

plugins {
  java
  signing
  `maven-publish`
  wrapper
  // id("plugins.common")
  // id("gg.jte.gradle")
  id("com.diffplug.spotless")
  id("io.github.gradle-nexus.publish-plugin")
}

val ktfmtVersion = libs.versions.ktfmt.get()
val gjfVersion = libs.versions.gjf.get()

// Formatting
spotless {
  java {
    googleJavaFormat(gjfVersion)
    target("**/*.java")
    targetExclude("**/build/**", "**/.gradle/**")
  }
  // if(plugins.hasPlugin(JavaPlugin::class.java)){ }

  kotlin {
    ktfmt(ktfmtVersion).googleStyle()
    target("**/*.kt")
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("**/build/**", "**/.gradle/**")
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktfmt(ktfmtVersion)
    target("**/*.gradle.kts")
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("**/build/**")
  }

  format("misc") {
    target("**/*.md", "**/.gitignore")
    trimTrailingWhitespace()
    indentWithSpaces(2)
    endWithNewline()
  }
  // isEnforceCheck = false
}

// jte {
//   contentType.set(ContentType.Plain)
//   generateNativeImageResources.set(true)
//   generate()
// }

tasks {
  // Generate build config.
  val buildConfig by registering(BuildConfig::class) { classFqName.set("BuildConfig") }
  sourceSets { main { java.srcDirs(buildConfig) } }

  // Fix "Execution optimizations have been disabled" warning for JTE
  // tasks.named("dokkaHtml") { dependsOn(tasks.generateJte) }

  wrapper {
    gradleVersion = libs.versions.gradle.asProvider().get()
    distributionType = Wrapper.DistributionType.ALL
  }

  // Default task (--rerun-tasks --no-build-cache)
  defaultTasks("clean", "tasks", "--all")

  // signing {
  //   setRequired({ signPublications == "true" })
  //   sign(publishing.publications["maven"])
  // }
}
