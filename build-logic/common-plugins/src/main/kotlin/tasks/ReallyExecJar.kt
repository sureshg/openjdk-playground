package tasks

import dev.suresh.gradle.*
import java.io.*
import java.nio.file.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.language.base.plugins.*

/**
 * The stub script is copied from
 * [Java Stack Trace Grouper](https://github.com/keith-turner/JSG/blob/master/src/main/scripts/stub.sh)
 * project.
 */
abstract class ReallyExecJar : DefaultTask() {

  @get:InputFile abstract val jarFile: RegularFileProperty

  @get:Input abstract val javaOpts: ListProperty<String>

  @get:[OutputFile Optional]
  abstract val execJarFile: RegularFileProperty

  init {
    description = "Build executable binary"
    group = LifecycleBasePlugin.BUILD_TASK_NAME
    javaOpts.convention(emptyList())
    // Default executable file name would be the project name.
    execJarFile.convention(project.layout.buildDirectory.file(project.name))
  }

  @TaskAction
  fun execute() {
    // Replace the tmp path with env variable for portability.
    val shellStub =
        javaClass
            .getResourceAsStream("/exec-jar-stub.sh")
            ?.readBytes()
            ?.decodeToString()
            ?.replace(
                oldValue = """"${'$'}JAVA_OPTS"""", newValue = javaOpts.get().joinToString(" "))
            ?.replace(tmp, "${'$'}TMPDIR/")
            ?: throw GradleException("Can't find executable shell stub!")
    logger.debug("Exec jar shell stub: $shellStub")
    // project.objects.property<Int>()

    val binFile = execJarFile.get().asFile
    logger.debug("Executable binary: ${binFile.name}")

    FileOutputStream(binFile).buffered().use { bos ->
      bos.write(shellStub.encodeToByteArray())
      Files.copy(jarFile.get().asFile.toPath(), bos)
    }
    binFile.setExecutable(true)
    logger.quiet("Executable Binary: ${binFile.path} ${binFile.displaySize}")
  }
}
