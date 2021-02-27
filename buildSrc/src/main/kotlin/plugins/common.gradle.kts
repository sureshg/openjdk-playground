package plugins

plugins {
    java
}

val templateOutDir = "generated-sources/templates/kotlin/main"

/**
 * Generate template classes.
 */
val copyTemplates by tasks.registering(Copy::class) {
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