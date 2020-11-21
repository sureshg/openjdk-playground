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
    val version = providers.gradleProperty("version")
        .forUseAtConfigurationTime()
        .get()

    val configMap = mapOf(
        "projectVersion" to version,
        "kotlinVersion" to System.getProperty("KotlinVersion")
    )

    inputs.property("buildversions", configMap.hashCode())
    from(layout.projectDirectory.dir("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/templates/kotlin/main"))
    expand(configMap)
    filteringCharset = "UTF-8"
}