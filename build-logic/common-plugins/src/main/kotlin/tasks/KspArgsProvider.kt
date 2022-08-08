package tasks

import org.gradle.api.tasks.*
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

/** Commandline args provider for KSP task. */
class KspArgsProvider(
  @InputFile @PathSensitive(PathSensitivity.RELATIVE)
  val input: File
) : CommandLineArgumentProvider {
  override fun asArguments() = listOf("proc.arg1=${input.path}")
}
