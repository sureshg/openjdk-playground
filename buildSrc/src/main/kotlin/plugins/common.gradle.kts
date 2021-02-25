package plugins

plugins {
    java
}

val templateOutDir = "generated-sources/templates/kotlin/main"

/**
 * Generate template classes.
 */
val copyTemplates by tasks.registering(Copy::class) {
    val version = providers
        .gradleProperty("version")
        .forUseAtConfigurationTime()
        .get()

    val props = project.extra.properties
    props["projectVersion"] = version
    props["kotlinVersion"] = System.getProperty("kotlinVersion")

    filteringCharset = "UTF-8"
    inputs.property("buildversions", props.hashCode())
    from(layout.projectDirectory.dir("src/main/templates"))
    into(layout.buildDirectory.dir(templateOutDir))
    exclude { it.name.startsWith("jte") }
    expand(props)
}