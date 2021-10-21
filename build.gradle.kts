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
  gitProperties
  taskinfo
  checksum
  signing
  `maven-publish`
  nexusPublish
  gradleRelease
  binCompatValidator
  dependencyAnalyze
  extraJavaModuleInfo
  licensee
  buildkonfig
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
    "-XshowSettings:all",
    "-Xmx128M",
    "-XX:+PrintCommandLineFlags",
    "-XX:+UseZGC",
    "-Xlog:gc*:$xQuote$tmp/$name-gc-%p-%t.log$xQuote:time,uptime,level,tid,tags:filecount=5,filesize=10m",
    "-XX:StartFlightRecording:gc=detailed,filename=$tmp/$name.jfr,settings=profile.jfc,name=$name,maxsize=100m,dumponexit=true",
    "-XX:FlightRecorderOptions:stackdepth=128",
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-XX:HeapDumpPath=$tmp/$name-%p.hprof",
    "-XX:ErrorFile=$tmp/$name-hs-err-%p.log",
    "-Dfile.encoding=UTF-8",
    "-Djava.awt.headless=true",
    "-Djdk.attach.allowAttachSelf=true",
    "-Djdk.tracePinnedThreads=full",
    "-Djava.security.egd=file:/dev/./urandom",
    "-Djdk.includeInExceptions=hostInfo,jar",
    "-XX:+UnlockDiagnosticVMOptions",
    "-XX:+ShowHiddenFrames",
    "-ea",
    // "-verbose:module",
    // "-XX:ConcGCThreads=2",
    // "-XX:ZUncommitDelay=60",
    // "-Xlog:gc\*",
    // "-Xlog:cds=debug",
    // "-Xlog:class+load=info",
    // "-XX:+IgnoreUnrecognizedVMOptions",
    // "-XX:NativeMemoryTracking=summary",
    // "-Dfile.encoding=COMPAT", // uses '-Dnative.encoding'
    // "-Djava.net.preferIPv4Stack=true",
    // "-Djavax.net.debug=all",
    // "-Djava.security.manager=disallow",
    // "-Djgitver.skip=true",
    // "-Djdk.lang.Process.launchMechanism=vfork",
    // "-Djdk.tls.maxCertificateChainLength=15",
    // "--add-exports=java.management/sun.management=ALL-UNNAMED",
    // "--add-exports=jdk.attach/sun.tools.attach=ALL-UNNAMED",
    // "--add-opens=java.base/java.net=ALL-UNNAMED",
    // "--add-opens=jdk.attach/sun.tools.attach=ALL-UNNAMED",
    // "--add-modules", "jdk.incubator.foreign", "--enable-native-access=ALL-UNNAMED"
  )
  // https://chriswhocodes.com/hotspot_options_openjdk18.html
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
    java.srcDirs(tasks.copyTemplates.get().destinationDir)
  }
}

kotlin {
  sourceSets.all {
    languageSettings.apply {
      apiVersion = kotlinApiVersion
      languageVersion = kotlinLangVersion
      progressiveMode = true
      enableLanguageFeature(LanguageFeature.JvmRecordSupport.name)
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
  java {
    googleJavaFormat(gjfVersion)
    // Exclude sealed types until it supports.
    targetExclude("**/ResultType.java", "build/generated-sources/**/*.java")
    importOrder()
    removeUnusedImports()
    toggleOffOn()
    trimTrailingWhitespace()
  }

  val ktlintConfig = mapOf(
    "disabled_rules" to "no-wildcard-imports",
    "insert_final_newline" to "true",
    "end_of_line" to "lf",
    "indent_size" to "2",
  )

  kotlin {
    ktlint(ktlintVersion).userData(ktlintConfig)
    targetExclude("$buildDir/**/*.kt", "bin/**/*.kt", "build/generated-sources/**/*.kt")
    endWithNewline()
    indentWithSpaces()
    trimTrailingWhitespace()
    // licenseHeader(rootProject.file("gradle/license-header.txt"))
  }

  kotlinGradle {
    ktlint(ktlintVersion).userData(ktlintConfig)
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

release {
  revertOnFail = true
}

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

// After the project configure
afterEvaluate {
  println("=== Project Configuration Completed ===")
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
          // "-XX:+IgnoreUnrecognizedVMOptions",
          // "--add-exports",
          // "java.base/sun.nio.ch=ALL-UNNAMED",
        )
      )
    }
  }

  // Configure "compileKotlin" and "compileTestKotlin" tasks.
  withType<KotlinCompile>().configureEach {
    usePreciseJavaTracking = true
    kotlinOptions {
      verbose = true
      jvmTarget = kotlinJvmTarget
      javaParameters = true
      incremental = true
      allWarningsAsErrors = false
      freeCompilerArgs += listOf(
        "-progressive",
        "-Xjsr305=strict",
        "-Xjvm-default=enable",
        "-Xassertions=jvm",
        "-Xallow-result-return-type",
        "-Xstrict-java-nullability-assertions",
        "-Xgenerate-strict-metadata-version",
        "-Xemit-jvm-type-annotations",
        // "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
        // "-Xexplicit-api={strict|warning|disable}",
        // "-Xjvm-enable-preview",
      )
    }

    // Generate kotlin templates. This has to be before kotlin compile.
    dependsOn(copyTemplates)
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
    dependsOn(test)
  }

  // Javadoc
  javadoc {
    isFailOnError = true
    // modularity.inferModulePath.set(true)
    (options as CoreJavadocOptions).apply {
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
      ghActionOutput("version", project.version)
      ghActionOutput("uberjar_name", fatJar.name)
      ghActionOutput("uberjar_path", fatJar.absolutePath)
    }
  }

  // Release depends on publish.
  afterReleaseBuild {
    dependsOn(publish)
  }

  dependencyUpdates {
    checkForGradleUpdate = true
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
    // Disallow release candidates as upgradable versions from stable versions
    // rejectVersionIf { candidate.version.isNonStable && !currentVersion.isNonStable }
  }

  // Disable dependency analysis
  analyzeDependencies.get().enabled = false
  analyzeClassesDependencies.get().enabled = false
  analyzeTestClassesDependencies.get().enabled = false

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

  // Default task
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
  implementation(Deps.TemplateEngine.Jte.jte)

  implementation(Deps.Jetty.LoadGen.client)
  implementation(Deps.Network.jmdns)
  implementation(Deps.Security.password4j) {
    exclude(group = "org.slf4j", module = "slf4j-nop")
  }
  implementation(Deps.Security.otp)
  implementation(Deps.Security.jwtJava)
  implementation(Deps.Cli.textTree)
  implementation(Deps.Foojay.discoclient)

  compileOnly(Deps.TemplateEngine.Jte.kotlin)
  compileOnly(Deps.Kotlinx.atomicfu)
  kapt(Deps.Google.AutoService.processor)

  // implementation(platform("org.apache.maven.resolver:maven-resolver:1.4.1"))
  // implementation("org.apache.maven:maven-resolver-provider:3.8.1")
  // implementation(fileTree("lib") { include("*.jar") })

  testImplementation(Deps.Kotlin.Coroutines.jdk8)
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
      artifact(dokkaHtmlJar.get())
      // artifact(tasks.shadowJar.get())

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

    // Github Package Registry
    register<MavenPublication>("gpr") {
      from(components["java"])
    }
  }
}
