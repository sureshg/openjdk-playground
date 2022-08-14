package plugins

import dev.suresh.gradle.libs

plugins {
  signing
  `maven-publish`
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
    targetExclude("**/spotless.java", "**/build/**", "**/.gradle/**")
  }
  // if(plugins.hasPlugin(JavaPlugin::class.java)){ }

  kotlin {
    ktfmt(ktfmtVersion).googleStyle()
    target("**/*.kt")
    trimTrailingWhitespace()
    endWithNewline()
    targetExclude("**/spotless.kt", "**/build/**", "**/.gradle/**")
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
