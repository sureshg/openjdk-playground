import org.gradle.api.tasks.testing.logging.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    application
    kotlinJvm
    kotlinxSerialization
    googleJib
    shadow
    spotless
    spotlessChangelog
    benmanesVersions
    gitProperties
    `maven-publish`
    mavenPublishAuth
    gradleRelease
}

group = "dev.suresh"
description = "OpenJDK latest release playground!"
val gitUrl: String by project
val jdkVersion = JavaVersion.toVersion(16)

application {
    mainClassName = "dev.suresh.Main"
    applicationDefaultJvmArgs += listOf(
        "-showversion",
        "--enable-preview",
        "-XX:+PrintCommandLineFlags",
        "-XX:+UseZGC",
        "-Djava.security.egd=file:/dev/./urandom"
    )
}

java {
    sourceCompatibility = jdkVersion
    targetCompatibility = jdkVersion
    modularity.inferModulePath.set(false)
    // withSourcesJar()
    // withJavadocJar()
}

jib {
    from {
        image = "openjdk:15-jdk-slim"
    }

    to {
        image = "sureshg/${project.name}"
        tags = setOf(project.version.toString(), "latest")
    }
    container {
        mainClass = application.mainClassName
        jvmFlags = application.applicationDefaultJvmArgs.toList()
    }
}

spotless {
    java {
        googleJavaFormat(Versions.googleJavaFormat)
    }

    kotlin {
        ktlint(Versions.ktlint).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
        targetExclude("$buildDir/**/*.kt", "bin/**/*.kt")
        // licenseHeader(License.Apache)
    }

    kotlinGradle {
        ktlint(Versions.ktlint)
        target("*.gradle.kts")
    }

    format("misc") {
        target("**/*.md", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }

    isEnforceCheck = false
}

gitProperties {
    gitPropertiesDir = "${project.buildDir}/resources/main/META-INF/${project.name}"
    customProperties["kotlin"] = Versions.kotlin
}

release {
    revertOnFail = true
}

repositories {
    mavenCentral()
}

// For dependencies that are needed for development only,
// creates a devOnly configuration and add it.
val devOnly: Configuration by configurations.creating {
    // extendsFrom(configurations["testImplementation"])
    // or val testImplementation by configurations
}

configurations {
    devOnly
}

tasks {

    // Configure "compileJava" and "compileTestJava" tasks.
    withType<JavaCompile>().configureEach {
        options.apply {
            encoding = "UTF-8"
            isIncremental = true
            compilerArgs.addAll(
                listOf(
                    "--enable-preview",
                    "-Xlint:all",
                    "-parameters",
                    "--release", // Compile for the specified Java SE release platform APIs
                    jdkVersion.majorVersion
                )
            )
        }
    }

    // Configure "compileKotlin" and "compileTestKotlin" tasks.
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            jvmTarget = "13"
            javaParameters = true
            allWarningsAsErrors = true
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xjsr305=strict",
                "-Xjvm-default=enable",
                "-Xassertions=jvm",
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.serialization.ImplicitReflectionSerializer",
                "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
                "-Xjavac-arguments=--enable-preview"
            )
        }
    }

    // JUnit5
    test {
        useJUnitPlatform()
        jvmArgs("--enable-preview")
        classpath += devOnly
        testLogging {
            events = setOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.SKIPPED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
        reports.html.isEnabled = true
    }

    javadoc {
        // Preview feature will fail for javadoc
        isFailOnError = false
    }

    // Uber jar
    shadowJar {
        archiveClassifier.set("uber")
        description = "Create a fat JAR of $archiveFileName and runtime dependencies."
        mergeServiceFiles()
        // relocate("okio", "shaded.okio")
        doLast {
            val fatJar = archiveFile.get().asFile
            println("FatJar: ${fatJar.path} (${fatJar.length().toDouble() / (1_000 * 1_000)} MB)")
        }
    }

    // Release depends on publish.
    afterReleaseBuild {
        dependsOn(publish)
    }

    // Disallow release candidates as upgradable versions from stable versions
    dependencyUpdates {
        rejectVersionIf { candidate.version.isNonStable && !currentVersion.isNonStable }
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

    // Sources jar (lazy)
    val sourcesJar by registering(Jar::class) {
        //kotlin.sourceSets.main.get().kotlin
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    // Javadoc jar (lazy)
    val javadocJar by registering(Jar::class) {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
    }

    // Reproducible builds
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    // Gradle Wrapper
    wrapper {
        gradleVersion = Versions.gradle
        distributionType = Wrapper.DistributionType.BIN
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

dependencies {
    implementation(enforcedPlatform(Deps.kotlinBom))
    implementation(enforcedPlatform(Deps.okhttpBom))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.kotlinxSerializationRuntime)
    implementation(Deps.kotlinxSerializationproperties)
    implementation(Deps.okhttp)
    implementation(Deps.okhttpMockWebServer)
    implementation(Deps.okhttpTLS)
    implementation(Deps.okhttpLoggingInterceptor)
    implementation(Deps.retrofit)
    implementation(Deps.retrofitSerializationAdapter)
    implementation(Deps.clikt)
    implementation(Deps.mordant)
    implementation(Deps.slf4jApi)
    implementation(Deps.shrinkwrap)
    // implementation(platform("org.apache.maven.resolver:maven-resolver:1.4.1"))
    // implementation("org.apache.maven:maven-resolver-provider:3.6.3")
    testImplementation(Deps.coroutinesJdk8)
    testImplementation(Deps.junitJupiter)
    testImplementation(Deps.slf4jSimple)
    testImplementation(Deps.mockk)
}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }
    }

    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            // artifact(sourcesJar.get())
            artifact(tasks.shadowJar.get())

            pom {
                packaging = "jar"
                description.set(project.description)
                inceptionYear.set("2020")
                url.set(gitUrl)

                developers {
                    developer {
                        id.set("sureshg")
                        name.set("Suresh")
                        email.set("email@suresh.dev")
                        organization.set("Suresh")
                        organizationUrl.set("https://suresh.dev")
                    }
                }

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                scm {
                    url.set(gitUrl)
                    tag.set("HEAD")
                    connection.set("scm:git:$gitUrl.git")
                    developerConnection.set("scm:git:$gitUrl.git")
                }

                issueManagement {
                    system.set("github")
                    url.set("$gitUrl/issues")
                }
            }
        }
    }
}
