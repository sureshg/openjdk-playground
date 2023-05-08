import io.github.krakowski.jextract.JextractTask
import org.gradle.internal.os.OperatingSystem

plugins {
  id("plugins.kotlin")
  alias(libs.plugins.jextract)
}

group = libs.versions.group.get()

version = libs.versions.module.jdk.get()

tasks.withType(JextractTask::class) {
  header("${project.projectDir}/src/main/c/ioctl.h") {
    targetPackage = "org.unix.gen"
    className = "Linux"
    includes =
        listOf(
            "/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include",
            "/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include/sys/ioctl.h")
    //    functions = listOf("ioctl", "strerror")
    //    variables = listOf("errno")
    //    structs = listOf("winsize", "ttysize")
    //    constants =
    //        listOf(
    //            "TIOCGWINSZ",
    //            "TIOCGSIZE",
    //            "STDOUT_FILENO",
    //            "STDIN_FILENO",
    //            "STDERR_FILENO",
    //        )
  }

  enabled = project.hasProperty("enableJextract")
  onlyIf { OperatingSystem.current().isMacOsX }
}

dependencies { implementation(libs.invokebinder) }
