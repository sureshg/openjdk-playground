package tasks

import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*

abstract class SampleTask : DefaultTask() {

  @get:Input
  abstract val pattern: Property<String>

  @get:Input
  abstract val versions: MapProperty<String, String>

  @get:InputDirectory
  @get:Optional
  abstract val inputDirectory: DirectoryProperty

  @get:InputFile
  abstract val inputFile: RegularFileProperty

  @get:Internal
  val type = "Sample Task"

  @TaskAction
  fun execute() {
    println("Executing task $type")
  }
}
