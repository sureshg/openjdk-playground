package tasks

import mebiSize
import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.*
import org.gradle.api.component.*
import org.gradle.api.file.*
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.language.base.plugins.*
import java.io.*
import java.nio.file.*

abstract class ReallyExecJar : DefaultTask() {

    @get:InputFile
    abstract val jarFile: RegularFileProperty

    @get:Input
    abstract val javaOpts: ListProperty<String>

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
        // project.objects.property<String>()
        val shellStub = javaClass.getResourceAsStream("/exec-jar-stub.sh")
            ?.readBytes()
            ?.decodeToString()
            ?.replace(
                "${'$'}JAVA_OPTS",
                javaOpts.get().joinToString(" ")
            ) ?: throw GradleException("Can't find executable shell stub!")
        logger.debug("Exec jar shell stub: $shellStub")

        val binFile = execJarFile.get().asFile
        logger.debug("Executable binary: ${binFile.name}")

        FileOutputStream(binFile).buffered().use { bos ->
            bos.write(shellStub.encodeToByteArray())
            Files.copy(jarFile.get().asFile.toPath(), bos)
        }
        binFile.setExecutable(true)
        logger.quiet("Executable Binary: ${binFile.path} ${binFile.mebiSize}")
    }
}