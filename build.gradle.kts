import gg.jte.*
import org.gradle.api.tasks.testing.logging.*
import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.net.*

plugins {
    id("plugins.common")
    jgitPlugin
    ksp
    kotlinJvm
    kotlinKapt
    kotlinxSerialization
    dokka
    jte
    protobuf
    googleJib
    shadow
    spotless
    qodanaPlugin
    jacoco
    redacted
    kotlinPowerAssert
    spotlessChangelog
    benmanesVersions
    versionCatalogUpdate
    gitProperties
    taskinfo
    checksum
    signing
    `maven-publish`
    nexusPublish
    binCompatValidator
    dependencyAnalysis
    extraJavaModuleInfo
    licensee
    buildkonfig
    // gradleRelease
    // kotlinxAtomicfu
    // plugins.common
}

val appMainModule: String by project
val appMainClass: String by project

application {
    mainClass.set(appMainClass)
    // mainModule.set(appMainModule)
    applicationDefaultJvmArgs += listOf(
        "--show-version",
        "--enable-preview",
        "--show-module-resolution",
        "--add-modules=$addModules",
        "--enable-native-access=ALL-UNNAMED",
        "-XshowSettings:all",
        "-Xmx128M",
        "-XX:+PrintCommandLineFlags",
        "-XX:+UseZGC",
        "-Xlog:cds,safepoint,gc*:file=$xQuote$tmp/$name-gc-%p-%t.log$xQuote:level,tags,time,uptime,pid,tid:filecount=5,filesize=10m", // os+thread,gc+heap=trace,
        "-XX:StartFlightRecording:settings=profile.jfc,memory-leaks=gc-roots,gc=detailed,jdk.ObjectCount\\#enabled=true,filename=$tmp/$name.jfr,name=$name,maxsize=100M,dumponexit=true",
        "-XX:FlightRecorderOptions:stackdepth=64",
        "-XX:+HeapDumpOnOutOfMemoryError",
        "-XX:HeapDumpPath=$tmp/$name-%p.hprof",
        "-XX:ErrorFile=$tmp/$name-hs-err-%p.log",
        "-Djava.awt.headless=true",
        "-Djdk.attach.allowAttachSelf=true",
        "-Djdk.tracePinnedThreads=full",
        "-Djava.security.egd=file:/dev/./urandom",
        "-Djdk.includeInExceptions=hostInfo,jar",
        "-Djava.security.egd=file:/dev/./urandom",
        "-XX:+UnlockDiagnosticVMOptions",
        "-XX:+LogVMOutput",
        "-XX:LogFile=$tmp/$name-jvm.log",
        "-XX:NativeMemoryTracking=summary",
        "-XX:+ShowHiddenFrames",
        "-ea"
        // "-verbose:module",
        // "-XX:ConcGCThreads=2",
        // "-XX:ZUncommitDelay=60",
        // "-XX:VMOptionsFile=vm_options",
        // "-Xlog:gc\*",
        // "-Xlog:class+load=info,cds=debug,cds+dynamic=info",
        // "-XX:+IgnoreUnrecognizedVMOptions",
        // "-XX:MaxRAMPercentage=0.8",
        // "-XX:+AutoCreateSharedArchive",
        // "-XX:SharedArchiveFile=$name.jsa",
        // "-XX:+StartAttachListener", // For jcmd Dynamic Attach Mechanism
        // "-XX:+DisableAttachMechanism",
        // "-XX:+DebugNonSafepoints",
        // "-Duser.timezone=\"PST8PDT\"",
        // "-Djava.net.preferIPv4Stack=true",
        // "-Djavax.net.debug=all",
        // "-Dhttps.protocols=TLSv1.2",
        // "-Dhttps.agent=$name",
        // "-Dhttp.keepAlive=true",
        // "-Dhttp.maxConnections=5",
        // "-Djava.security.manager=allow",
        // "-Dfile.encoding=COMPAT", // uses '-Dnative.encoding'
        // "-Djava.io.tmpdir=/var/data/tmp",
        // "-Djava.locale.providers=COMPAT,CLDR",
        // "-Djgitver.skip=true",
        // "-Djdk.lang.Process.launchMechanism=vfork",
        // "-Djdk.tls.maxCertificateChainLength=10",
        // "-Djdk.tls.maxHandshakeMessageSize=32768",
        // "--add-exports=java.management/sun.management=ALL-UNNAMED",
        // "--add-exports=jdk.attach/sun.tools.attach=ALL-UNNAMED",
        // "--add-opens=java.base/java.net=ALL-UNNAMED",
        // "--add-opens=jdk.attach/sun.tools.attach=ALL-UNNAMED",
        // "--patch-module java.base="$DIR/jsr166.jar",
        // "-javaagent:path/to/glowroot.jar",
        // "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
        // "-agentlib:jdwp=transport=dt_socket,server=n,address=host:5005,suspend=y,onthrow=<FQ exception class name>,onuncaught=<y/n>"
    )
    // https://docs.oracle.com/en/java/javase/18/docs/specs/man/java.html
    // https://cs.oswego.edu/dl/jsr166/dist/jsr166.jar
    // https://chriswhocodes.com/hotspot_options_openjdk19.html
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
        vendor.set(JvmVendorSpec.ORACLE)
    }
    // modularity.inferModulePath.set(true)
}

// Add the generated templates to source set.
sourceSets {
    main {
        java.srcDirs(tasks.copyTemplates)
    }
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            apiVersion = kotlinApiVersion
            languageVersion = kotlinLangVersion
            progressiveMode = true
            enableLanguageFeature(LanguageFeature.JvmRecordSupport.name)
            enableLanguageFeature(LanguageFeature.ContextReceivers.name)
            optIn("kotlin.RequiresOptIn")
            optIn("kotlin.ExperimentalStdlibApi")
            optIn("kotlin.ExperimentalUnsignedTypes")
            optIn("kotlin.io.path.ExperimentalPathApi")
            optIn("kotlin.time.ExperimentalTime")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlinx.coroutines.FlowPreview")
            optIn("kotlinx.serialization.ExperimentalSerializationApi")
            optIn("kotlin.ExperimentalMultiplatform")
            optIn("kotlin.js.ExperimentalJsExport")
        }
        // kotlin.srcDirs()
    }

    jvmToolchain {
        (this as JavaToolchainSpec).apply {
            languageVersion.set(java.toolchain.languageVersion.get())
            vendor.set(java.toolchain.vendor.get())
        }
    }

    // kotlinDaemonJvmArgs = listOf("--show-version", "--enable-preview")
    // explicitApi()
}

ksp {
}

kapt {
    javacOptions {
        option("--enable-preview")
        option("-Xmaxerrs", 200)
    }
}

jte {
    contentType.set(ContentType.Plain)
    generateNativeImageResources.set(true)
    generate()
}

kotlinPowerAssert {
    functions = listOf("kotlin.assert", "kotlin.test.assertTrue")
}

redacted {
    redactedAnnotation.set("Redacted")
    enabled.set(false)
}

apiValidation {
    validationDisabled = true
}

// Formatting
spotless {
    // if(plugins.hasPlugin(JavaPlugin::class.java)){}
    java {
        googleJavaFormat(gjfVersion)
        // Exclude sealed types until it supports.
        targetExclude("**/ResultType.java", "build/generated-sources/**/*.java")
        importOrder()
        removeUnusedImports()
        toggleOffOn()
        trimTrailingWhitespace()
    }

    val ktlintConfig = mapOf("disabled_rules" to "no-wildcard-imports")
    kotlin {
        ktlint(ktlintVersion).setUseExperimental(true).userData(ktlintConfig)
        targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "build/generated-sources/**/*.kt")
        endWithNewline()
        indentWithSpaces()
        trimTrailingWhitespace()
        // licenseHeader(rootProject.file("gradle/license-header.txt"))
    }

    kotlinGradle {
        ktlint(ktlintVersion).setUseExperimental(true).userData(ktlintConfig)
        target("*.gradle.kts")
    }

    format("misc") {
        target("**/*.md", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
    // isEnforceCheck = false
}

qodana {
    autoUpdate.set(true)
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
        mainClass = application.mainClass.get()
        jvmFlags = application.applicationDefaultJvmArgs.toList()
    }
}

// val branch_name: String  by extra
jgitver {
    useSnapshot = true
    nonQualifierBranches = "main"
}

gitProperties {
    gitPropertiesDir.set(project.layout.buildDirectory.dir("resources/main/META-INF/${project.name}"))
    customProperties["kotlin"] = kotlinVersion
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    if (GithubAction.isEnabled) {
        publishAlways()
        isUploadInBackground = false
        tag("GITHUB_ACTION")
        buildScanPublished {
            GithubAction.setOutput("build_scan_uri", buildScanUri)
            GithubAction.notice(
                buildScanUri.toASCIIString(),
                "${GithubAction.Env.RUNNER_OS} BuildScan URL"
            )
        }
    }
}

// release {
//  revertOnFail = true
// }

// Create ShadowJar specific runtimeClasspath.
val shadowRuntime: Configuration by configurations.creating {
    val runtimeClasspath by configurations.getting
    extendsFrom(runtimeClasspath)
    attributes { attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME)) }
}

// For dependencies that are needed for development only.
val devOnly: Configuration by configurations.creating

// Deactivate java-module-info plugin for all configs
configurations {
    // runtimeClasspath...etc
    all {
        attributes {
            attribute(Attribute.of("javaModule", Boolean::class.javaObjectType), false)
        }
    }
}

tasks {
    // Configure "compileJava" and "compileTestJava" tasks.
    withType<JavaCompile>().configureEach {
        options.apply {
            encoding = "UTF-8"
            release.set(javaVersion)
            isIncremental = true
            isFork = true
            debugOptions.debugLevel = "source,lines,vars"
            // For Gradle worker daemon.
            forkOptions.jvmArgs?.addAll(jvmArguments)
            compilerArgs.addAll(
                listOf(
                    "--enable-preview",
                    "-Xlint:all",
                    "-parameters",
                    "--add-modules=$addModules"
                    // "-XX:+IgnoreUnrecognizedVMOptions",
                    // "--add-exports",
                    // "java.base/sun.nio.ch=ALL-UNNAMED",
                    // "--patch-module",
                    // "$moduleName=${sourceSets.main.get().output.asPath}"
                )
            )
        }
    }

    /* Configure "compileKotlin" and "compileTestKotlin" tasks.
     * JVM backend compiler options can be found in,
     * https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JVMCompilerArguments.kt
     * https://github.com/JetBrains/kotlin/blob/master/compiler/config.jvm/src/org/jetbrains/kotlin/config/JvmTarget.kt
     */
    withType<KotlinCompile>().configureEach {
        usePreciseJavaTracking = true
        kotlinOptions {
            verbose = true
            jvmTarget = kotlinJvmTarget
            javaParameters = true
            incremental = true
            allWarningsAsErrors = false
            freeCompilerArgs += listOf(
                "-Xjsr305=strict",
                "-Xjvm-default=all",
                "-Xassertions=jvm",
                "-Xallow-result-return-type",
                "-Xemit-jvm-type-annotations",
                "-Xjspecify-annotations=strict",
                "-Xadd-modules=$addModules"
                // "-Xjvm-enable-preview",
                // "-Xadd-modules=ALL-MODULE-PATH",
                // "-Xmodule-path=",
                // "-Xjdk-release=$kotlinJvmTarget",
                // "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
                // "-Xexplicit-api={strict|warning|disable}",
                // "-Xgenerate-strict-metadata-version",
            )
        }
    }

    run.invoke {
        args(true)
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
        reports.html.required.set(true)
    }

    // Code Coverage
    jacocoTestReport {
        reports {
            html.required.set(true)
        }
    }

    // Javadoc
    javadoc {
        isFailOnError = true
        // modularity.inferModulePath.set(true)
        (options as CoreJavadocOptions).apply {
            encoding = "UTF-8"
            addBooleanOption("-enable-preview", true)
            addStringOption("-release", javaVersion.toString())
        }
    }

    // Dokka config
    withType<DokkaTask>().configureEach {
        outputDirectory.set(buildDir.resolve("dokka"))
        moduleName.set(project.name)

        dokkaSourceSets {
            configureEach {
                displayName.set("JVM")
                includes.from("README.md")
                jdkVersion.set(kotlinJvmTarget.toInt())
                noStdlibLink.set(false)
                noJdkLink.set(false)
                // sourceRoots.setFrom(file("src/main/kotlin"))

                sourceLink {
                    localDirectory.set(file("src/main/kotlin"))
                    remoteUrl.set(URL("$githubProject/tree/main/src/main/kotlin"))
                    remoteLineSuffix.set("#L")
                }

                externalDocumentationLink {
                    url.set(URL("https://kotlin.github.io/kotlinx.coroutines/package-list"))
                }

                perPackageOption {
                    matchingRegex.set("kotlin($|\\.).*")
                    skipDeprecated.set(false)
                    reportUndocumented.set(true) // Emit warnings about not documented members
                    includeNonPublic.set(false)
                }
            }
        }
    }

    // Uber jar
    shadowJar {
        archiveClassifier.set("uber")
        description = "Create a fat JAR of $archiveFileName and runtime dependencies."
        mergeServiceFiles()

        // Don't create modular shadow jar
        // exclude("module-info.class")

        // relocate("okio", "shaded.okio")
        // configurations = listOf(shadowRuntime)

        doLast {
            val fatJar = archiveFile.get().asFile
            println("FatJar: ${fatJar.path} (${fatJar.length().toDouble() / (1_000 * 1_000)} MB)")
            println(appRunCmd(fatJar.toPath(), application.applicationDefaultJvmArgs.toList()))
            GithubAction.setOutput("version", project.version)
            GithubAction.setOutput("uberjar_name", fatJar.name)
            GithubAction.setOutput("uberjar_path", fatJar.absolutePath)
        }
    }

    dependencyUpdates {
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
        // Disallow release candidates as upgradable versions from stable versions
        // rejectVersionIf { candidate.version.isNonStable && !currentVersion.isNonStable }
    }

    dependencyAnalysis {
        issues {
            all {
                onAny {
                    severity("warn")
                }
            }
        }
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

    // signing {
    //   setRequired({ signPublications == "true" })
    //   sign(publishing.publications["maven"])
    // }

    // Default task (--rerun-tasks --no-build-cache)
    defaultTasks("clean", "tasks", "--all")
}

// Dokka html doc
val dokkaHtmlJar by tasks.registering(Jar::class) {
    from(tasks.dokkaHtml)
    archiveClassifier.set("htmldoc")
}

// For publishing pure kotlin project
val emptyJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    // duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // manifest {
    //   attributes("Automatic-Module-Name" to appMainModule)
    // }
}

dependencies {
    implementation(platform(Deps.Kotlin.bom))
    implementation(platform(Deps.OkHttp.bom))
    implementation(Deps.Kotlin.stdlibJdk8)
    implementation(Deps.Kotlin.reflect)
    implementation(Deps.Kotlin.Coroutines.jdk8)
    implementation(Deps.Kotlinx.Serialization.json)
    implementation(Deps.Kotlinx.Serialization.properties)
    implementation(Deps.Kotlinx.dateTime)
    implementation(Deps.Jetty.server) {
        version { strictly(Deps.Jetty.version) }
    }
    implementation(Deps.Jetty.jakartaServletApi)
    implementation(Deps.Jetty.servlet)
    implementation(Deps.Http.urlbuilder)
    implementation(Deps.OkHttp.okhttp)
    implementation(Deps.OkHttp.mockWebServer)
    implementation(Deps.OkHttp.tls)
    implementation(Deps.OkHttp.loggingInterceptor)
    implementation(Deps.Retrofit.retrofit)
    implementation(Deps.Retrofit.koltinxSerializationAdapter)
    implementation(Deps.Retry.kotlinRetry)
    implementation(Deps.Cli.clikt)
    implementation(Deps.Cli.mordant)
    implementation(Deps.Cli.crossword)
    implementation(Deps.Logging.Slf4j.api)
    implementation(Deps.Logging.Slf4j.simple)
    implementation(Deps.Maven.shrinkwrap)
    implementation(Deps.TLS.certifikit)
    implementation(Deps.Google.AutoService.annotations)
    implementation(Deps.Jackson.databind)
    implementation(Deps.Google.ApiService.sdmv1)
    implementation(Deps.TemplateEngine.Jte.runtime)

    implementation(Deps.Jetty.LoadGen.client)
    implementation(Deps.Network.jmdns)
    implementation(Deps.Security.password4j) {
        exclude(group = "org.slf4j", module = "slf4j-nop")
    }
    implementation(Deps.Security.otp)
    implementation(Deps.Security.jwtJava)
    implementation(Deps.Cli.textTree)
    // implementation(Deps.Foojay.discoclient)

    compileOnly(Deps.TemplateEngine.Jte.kotlin)
    compileOnly(Deps.Kotlinx.atomicfu)
    kapt(Deps.Google.AutoService.processor)

    // implementation(platform("org.apache.maven.resolver:maven-resolver:1.4.1"))
    // implementation("org.apache.maven:maven-resolver-provider:3.8.1")
    // implementation(fileTree("lib") { include("*.jar") })

    constraints {
        implementation("org.apache.logging.log4j:log4j-core") {
            version {
                prefer("[2.17,0[")
                strictly(Deps.Logging.Log4j2.version)
            }
            because("CVE-2021-44228 - Log4shell")
        }
    }

    testImplementation(Deps.Kotlin.Coroutines.test)
    testImplementation(platform(Deps.Junit.bom))
    testImplementation(Deps.Junit.jupiter)
    testImplementation(Deps.Junit.pioneer)
    testImplementation(kotlin("test-junit5"))

    testImplementation(Deps.KoTest.junit5Runner)
    testImplementation(Deps.KoTest.assertions)
    testImplementation(Deps.Logging.Slf4j.simple)
    testImplementation(Deps.Mock.mockk)

    // Dokka Plugins (dokkaHtmlPlugin, dokkaGfmPlugin)
    // dokkaPlugin(Deps.Dokka.kotlinAsJavaPlugin)
    dokkaPlugin(Deps.Dokka.mermaidPlugin)
}

publishing {
    repositories {
        maven {
            url = uri("$buildDir/repo")
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sureshg/${project.name}")
            credentials {
                username = project.findProperty("gpr.user") as? String ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as? String ?: System.getenv("TOKEN")
            }
        }
    }

    publications {
        // Maven Central
        register<MavenPublication>("maven") {
            from(components["java"])
            artifact(dokkaHtmlJar)
            // artifact(tasks.shadowJar)

            pom {
                packaging = "jar"
                description.set(project.description)
                inceptionYear.set("2021")
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
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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

        // GitHub Package Registry
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
