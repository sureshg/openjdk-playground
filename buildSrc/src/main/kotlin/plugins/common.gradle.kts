package plugins

plugins {
    java
}

/**
 * Add the generated templates to source set.
 */
sourceSets {
    main {
        java.srcDir(project.file("$buildDir/generated/sources/templates/kotlin/main"))
    }
}

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
    into(layout.buildDirectory.dir("generated/sources/templates/kotlin/main"))
    expand(props)
}