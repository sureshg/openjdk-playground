import org.gradle.api.tasks.testing.logging.*
import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.net.*

plugins {
    java
    application
    kotlinJvm
    kotlinKapt
    kotlinxSerialization
    dokka
    protobuf
    googleJib
    shadow
    spotless
    jacoco
    spotlessChangelog
    benmanesVersions
    gitProperties
    `maven-publish`
    mavenRepoAuth
    gradleRelease
}

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
    modularity.inferModulePath.set(false)
    withSourcesJar()
    withJavadocJar()
}

// Formatting
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
        ktlint(Versions.ktlint).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
        target("*.gradle.kts")
    }

    format("misc") {
        target("**/*.md", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }

    // isEnforceCheck = false
}

gitProperties {
    gitPropertiesDir = "${project.buildDir}/resources/main/META-INF/${project.name}"
    customProperties["kotlin"] = kotlinVersion
}

jib {
    from {
        image = "openjdk:$javaVersion-jdk-slim"
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

release {
    revertOnFail = true
}

repositories {
    mavenCentral()
    jcenter()
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
            release.set(javaVersion)
            isIncremental = true
            isFork = true
            compilerArgs.addAll(
                listOf(
                    "--enable-preview",
                    "-Xlint:all",
                    "-parameters"
                    // "-XX:+IgnoreUnrecognizedVMOptions",
                )
            )
        }
    }

    // Configure "compileKotlin" and "compileTestKotlin" tasks.
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            jvmTarget = kotlinJvmTarget
            languageVersion = kotlinLangVersion
            javaParameters = true
            incremental = true
            jdkHome = System.getProperty("java.home")
            useIR = true
            allWarningsAsErrors = false
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xjsr305=strict",
                "-Xjvm-default=enable",
                "-Xassertions=jvm",
                "-Xinline-classes",
                "-XXLanguage:+NewInference",
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlin.ExperimentalStdlibApi",
                "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
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
            showStandardStreams = true
        }
        reports.html.isEnabled = true
    }

    // Code Coverage
    jacocoTestReport {
        reports {
            html.isEnabled = true
        }
        dependsOn(test)
    }

    // Javadoc
    javadoc {
        isFailOnError = true
        (options as CoreJavadocOptions).apply {
            addBooleanOption("-enable-preview", true)
            addStringOption("-release", javaVersion.toString())
        }
    }

    // Dokka config
    withType<DokkaTask>().configureEach {
        outputDirectory.set(buildDir.resolve("dokka"))
        dokkaSourceSets.configureEach {
            moduleDisplayName.set(project.name)
            includes.from("README.md")
            jdkVersion.set(kotlinJvmTarget.toInt())
            noStdlibLink.set(false)
            noJdkLink.set(false)
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("$githubProject/tree/master/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
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

    // Reproducible builds
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    // Gradle Wrapper
    wrapper {
        gradleVersion = gradleRelease
        distributionType = Wrapper.DistributionType.ALL
    }

    // Default task
    defaultTasks("clean", "tasks", "--all")
}

// Dokka html doc
val dokkaHtmlJar by tasks.registering(Jar::class) {
    from(tasks.dokkaHtml)
    archiveClassifier.set("htmldoc")
}

dependencies {
    implementation(enforcedPlatform(Deps.kotlinBom))
    implementation(enforcedPlatform(Deps.okhttpBom))
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.kotlinxSerializationCore)
    implementation(Deps.kotlinxSerializationproperties)
    implementation(Deps.Jetty.server)
    implementation(Deps.okhttp)
    implementation(Deps.okhttpMockWebServer)
    implementation(Deps.okhttpTLS)
    implementation(Deps.okhttpLoggingInterceptor)
    implementation(Deps.retrofit)
    implementation(Deps.retrofitSerializationAdapter)
    implementation(Deps.clikt)
    implementation(Deps.certifikit)
    implementation(Deps.mordant)
    implementation(Deps.slf4jApi)
    implementation(Deps.shrinkwrap)
    compileOnly(Deps.kotlinxAtomicfu)
    // implementation(platform("org.apache.maven.resolver:maven-resolver:1.4.1"))
    // implementation("org.apache.maven:maven-resolver-provider:3.6.3")
    testImplementation(Deps.coroutinesJdk8)
    testImplementation(Deps.junitJupiter)
    testImplementation(Deps.slf4jSimple)
    testImplementation(Deps.mockk)

    // Dokka Plugins (dokkaHtmlPlugin, dokkaGfmPlugin)
    // dokkaPlugin(Deps.Dokka.kotlinAsJavaPlugin)
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
            artifact(dokkaHtmlJar.get())
            // artifact(tasks.shadowJar.get())

            pom {
                packaging = "jar"
                description.set(project.description)
                inceptionYear.set("2020")
                url.set(githubProject)

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
                    url.set(githubProject)
                    tag.set("HEAD")
                    connection.set("scm:git:$githubProject.git")
                    developerConnection.set("scm:git:$githubProject.git")
                }

                issueManagement {
                    system.set("github")
                    url.set("$githubProject/issues")
                }
            }
        }
    }
}
