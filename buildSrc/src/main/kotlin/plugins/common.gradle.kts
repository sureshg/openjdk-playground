package plugins

import org.gradle.jvm.tasks.Jar
import java.io.*
import java.util.spi.*

plugins {
    java
}

val templateOutDir = "generated-sources/templates/kotlin/main"

tasks {

    val printModuleDeps by registering {
        description = "Print Java Platform Module dependencies of the application."
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        doLast {
            val uberJar = named("shadowJar", Jar::class)
            val jarFile = uberJar.get().archiveFile.get().asFile

            val jdeps = ToolProvider.findFirst("jdeps").orElseGet { error("") }
            val out = StringWriter()
            val pw = PrintWriter(out)
            jdeps.run(pw, pw, "--print-module-deps", "--ignore-missing-deps", jarFile.absolutePath)

            val modules = out.toString()
            println(modules)
        }
        dependsOn("shadowJar")
    }

    val copyTemplates by registering(Copy::class) {
        description = "Generate template classes."
        group = LifecycleBasePlugin.BUILD_TASK_NAME

        val configuredVersion = providers
            .gradleProperty("version")
            .forUseAtConfigurationTime()
            .get()

        val props = project.extra.properties
        props["projectVersion"] = project.version
        props["configuredVersion"] = configuredVersion
        props["kotlinVersion"] = System.getProperty("kotlinVersion")

        val debug: String by project
        if (debug.toBoolean()) {
            props.forEach { (t, u) ->
                println("%1\$-42s --> %2\$s".format(t, u))
            }
        }

        filteringCharset = "UTF-8"
        inputs.property("buildversions", props.hashCode())
        from(layout.projectDirectory.dir("src/main/templates"))
        into(layout.buildDirectory.dir(templateOutDir))
        exclude { it.name.startsWith("jte") }
        expand(props)
    }
}
