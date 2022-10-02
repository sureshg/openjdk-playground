import dev.suresh.gradle.configurePom
import dev.suresh.gradle.tmp
import dev.suresh.gradle.xQuote
import java.net.URL
import kotlinx.kover.api.*
import org.gradle.api.tasks.testing.logging.*
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("plugins.common")
  id("plugins.misc")
  jgitPlugin
  kotlin("jvm")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.ksp.redacted)
  alias(libs.plugins.ksp.powerassert)
  kover
  dokka
  googleJib
  shadow
  qodanaPlugin
  sonarqube
  spotlessChangelog
  versionCatalogUpdate
  gitProperties
  checksum
  binCompatValidator
  extraJavaModuleInfo
  licensee
  buildkonfig
  // gradleRelease
  // kotlinxAtomicfu
}

val appMainModule: String by project
val appMainClass: String by project

application {
  mainClass.set(appMainClass)
  // mainModule.set(appMainModule)
  applicationDefaultJvmArgs +=
      listOf(
          "--show-version",
          "--enable-preview",
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
          "-XX:OnOutOfMemoryError='kill -9 %p'",
          "-XX:+ExitOnOutOfMemoryError",
          "-Djava.awt.headless=true",
          "-Djdk.attach.allowAttachSelf=true",
          "-Djdk.tracePinnedThreads=full",
          "-Djava.security.egd=file:/dev/./urandom",
          "-Djdk.includeInExceptions=hostInfo,jar",
          "-XX:+UnlockDiagnosticVMOptions",
          "-XX:+LogVMOutput",
          "-XX:LogFile=$tmp/$name-jvm.log",
          "-XX:NativeMemoryTracking=summary",
          "-XX:+ShowHiddenFrames",
          "-ea",
          // "--show-module-resolution",
          // "-XX:+AutoCreateSharedArchive",
          // "-XX:SharedArchiveFile=$tmp/$name.jsa"
          // "-verbose:module",
          // "-XX:ConcGCThreads=2",
          // "-XX:ZUncommitDelay=60",
          // "-XX:VMOptionsFile=vm_options",
          // "-Xlog:gc\*",
          // "-Xlog:class+load=info,cds=debug,cds+dynamic=info",
          // "-XX:+IgnoreUnrecognizedVMOptions",
          // "-XX:MaxRAMPercentage=0.8",
          // "-XX:+StartAttachListener", // For jcmd Dynamic Attach Mechanism
          // "-XX:+DisableAttachMechanism",
          // "-XX:+DebugNonSafepoints",
          // "-XX:OnOutOfMemoryError="./restart.sh"",
          // "-XX:SelfDestructTimer=0.05",
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
          // "-agentlib:jdwp=transport=dt_socket,server=n,address=host:5005,suspend=y,onthrow=<FQ
          // exception class name>,onuncaught=<y/n>"
          )
  // https://docs.oracle.com/en/java/javase/18/docs/specs/man/java.html
  // https://cs.oswego.edu/dl/jsr166/dist/jsr166.jar
  // https://chriswhocodes.com/hotspot_options_openjdk19.html
}

kotlin {
  sourceSets.all {
    languageSettings.apply {
      apiVersion = kotlinApiVersion
      languageVersion = kotlinLangVersion
      progressiveMode = true
      enableLanguageFeature(LanguageFeature.JvmRecordSupport.name)
      enableLanguageFeature(LanguageFeature.ContextReceivers.name)
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
    // kotlin.setSrcDirs(listOf("src/kotlin"))
  }

  jvmToolchain {
    languageVersion.set(java.toolchain.languageVersion.get())
    vendor.set(java.toolchain.vendor.get())
  }
  // jvmToolchain(libs.versions.java.asProvider().get().toInt())

  // sourceSets { main { ... } }
  // kotlinDaemonJvmArgs = listOf("--show-version", "--enable-preview")
  // explicitApi()
}

ksp {
  arg("autoserviceKsp.verify", "true")
  arg("autoserviceKsp.verbose", "true")
  // arg(KspArgsProvider(project.layout.projectDirectory.file("config").asFile))
}

redacted { enabled.set(true) }

apiValidation { validationDisabled = true }

qodana { autoUpdate.set(true) }

kover {
  isDisabled.set(false)
  engine.set(DefaultIntellijEngine)
  filters { classes { excludes += listOf("dev.suresh.example.*") } }
}

koverMerged { enable() }

kotlinPowerAssert { functions = listOf("kotlin.assert", "kotlin.test.assertTrue") }

sonarqube {
  properties {
    properties["sonar.projectKey"] = "sureshg_openjdk-playground"
    properties["sonar.organization"] = "sureshg"
    properties["sonar.host.url"] = "https://sonarcloud.io"
  }
}

jib {
  from { image = "openjdk:$javaVersion-jdk-slim" }

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

jdeprscan { forRemoval.set(true) }

// Create ShadowJar specific runtimeClasspath.
val shadowRuntime: Configuration by
    configurations.creating {
      val runtimeClasspath by configurations.getting
      extendsFrom(runtimeClasspath)
      attributes { attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME)) }
    }

// For dependencies that are needed for development only.
val devOnly: Configuration by configurations.creating

// Deactivate java-module-info plugin for all configs
configurations {
  // runtimeClasspath...etc
  all { attributes { attribute(Attribute.of("javaModule", Boolean::class.javaObjectType), false) } }
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
              "--add-modules=$addModules",
              // "-Xlint:-deprecation", // suppress deprecations
              // "-XX:+IgnoreUnrecognizedVMOptions",
              // "--add-exports",
              // "java.base/sun.nio.ch=ALL-UNNAMED",
              // "--patch-module",
              // "$moduleName=${sourceSets.main.get().output.asPath}"
              ),
      )
    }
  }

  /* Configure "compileKotlin" and "compileTestKotlin" tasks.
   * JVM backend compiler options can be found in,
   * https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JVMCompilerArguments.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/config.jvm/src/org/jetbrains/kotlin/config/JvmTarget.kt
   * https://github.com/JetBrains/kotlin/blob/master/compiler/util/src/org/jetbrains/kotlin/config/ApiVersion.kt#L35
   */
  withType<KotlinCompile>().configureEach {
    usePreciseJavaTracking = true
    kotlinOptions {
      verbose = true
      jvmTarget = kotlinJvmTarget
      javaParameters = true
      incremental = true
      allWarningsAsErrors = false
      freeCompilerArgs +=
          listOf(
              "-Xadd-modules=$addModules",
              "-Xjsr305=strict",
              "-Xjvm-default=all",
              "-Xassertions=jvm",
              "-Xallow-result-return-type",
              "-Xemit-jvm-type-annotations",
              "-Xjspecify-annotations=strict",
              // "-Xjdk-release=$javaVersion",
              // "-Xuse-k2",
              // "-Xbackend-threads=4",
              // "-Xadd-modules=ALL-MODULE-PATH",
              // "-Xmodule-path=",
              // "-Xjvm-enable-preview",
              // "-Xjavac-arguments=\"--add-exports java.base/sun.nio.ch=ALL-UNNAMED\"",
              // "-Xexplicit-api={strict|warning|disable}",
              // "-Xgenerate-strict-metadata-version",
              )
    }
  }

  run.invoke { args(true) }

  // JUnit5
  test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
    classpath += devOnly

    testLogging {
      events =
          setOf(
              TestLogEvent.PASSED,
              TestLogEvent.FAILED,
              TestLogEvent.SKIPPED,
          )
      exceptionFormat = TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
      showStandardStreams = true
    }
    reports.html.required.set(true)

    // Configure coverage for test task.
    extensions.configure<KoverTaskExtension> {
      isDisabled.set(false)
      // excludes.addAll(...)
    }
  }

  testing { suites.getByName<JvmTestSuite>("test").useJUnitJupiter() }

  // Javadoc
  javadoc {
    isFailOnError = true
    // modularity.inferModulePath.set(true)
    (options as CoreJavadocOptions).apply {
      encoding = "UTF-8"
      addBooleanOption("-enable-preview", true)
      addStringOption("-release", javaVersion.toString())
      addStringOption("Xdoclint:none", "-quiet")
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
          remoteUrl.set(URL("${libs.versions.githubProject.get()}/tree/main/src/main/kotlin"))
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
  }
}

// Dokka html doc
val dokkaHtmlJar by
    tasks.registering(Jar::class) {
      from(tasks.dokkaHtml)
      archiveClassifier.set("htmldoc")
    }

// For publishing a pure kotlin project
val emptyJar by
    tasks.registering(Jar::class) {
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
  implementation(Deps.Kotlinx.dateTime)
  implementation(libs.jetty.server) { version { strictly(libs.versions.jetty.asProvider().get()) } }
  implementation(libs.jetty.jakarta.servlet)
  implementation(libs.jetty.servlet)
  implementation(Deps.Http.urlbuilder)
  implementation(Deps.OkHttp.okhttp)
  implementation(Deps.OkHttp.mockWebServer)
  implementation(Deps.OkHttp.tls)
  implementation(Deps.OkHttp.loggingInterceptor)
  implementation(Deps.Retry.kotlinRetry)
  implementation(Deps.Cli.clikt)
  implementation(Deps.Cli.mordant)
  implementation(Deps.Cli.crossword)
  implementation(libs.slf4j.api)
  implementation(libs.slf4j.simple)
  implementation(Deps.TLS.certifikit)
  implementation(libs.jackson.databind)
  implementation(libs.jte.runtime)
  implementation(Deps.Network.jmdns)
  implementation(Deps.Security.password4j) { exclude(group = "org.slf4j", module = "slf4j-nop") }
  implementation(Deps.Security.otp)
  implementation(libs.log4j.core)
  compileOnly(libs.jte.kotlin)
  compileOnly(Deps.Kotlinx.atomicfu)

  // Auto-service
  ksp(libs.ksp.auto.service)
  implementation(libs.google.auto.annotations)
  // kapt("com.google.auto.service:auto-service:1.0.1")

  // api(platform(projects.bom))
  // api(project(":preview-features/ffm-api"))
  implementation(libs.ffm.api)
  implementation(libs.maven.archeologist)

  testImplementation(Deps.Kotlin.Coroutines.test)
  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.pioneer)
  testImplementation(kotlin("test-junit5"))

  testImplementation(Deps.KoTest.junit5Runner)
  testImplementation(Deps.KoTest.assertions)
  testImplementation(libs.slf4j.simple)
  testImplementation(libs.mockk)

  // Dokka Plugins (dokkaHtmlPlugin, dokkaGfmPlugin)
  // dokkaPlugin(Deps.Dokka.kotlinAsJavaPlugin)
  dokkaPlugin(Deps.Dokka.mermaidPlugin)

  // implementation(fileTree("lib") { include("*.jar") })
  // implementation(platform("org.apache.maven.resolver:maven-resolver:1.4.1"))
  // implementation("org.apache.maven:maven-resolver-provider:3.8.1")

  //  constraints {
  //    implementation("org.apache.logging.log4j:log4j-core") {
  //      version {
  //        prefer("[2.18,0[")
  //        strictly(Deps.Logging.Log4j2.version)
  //      }
  //      because("CVE-2021-44228 - Log4shell")
  //    }
  //  }
}

publishing {
  repositories {
    maven {
      name = "local"
      url = uri(layout.buildDirectory.dir("repo"))
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
      artifact(tasks.buildExecutable)
      // artifact(tasks.shadowJar)
      configurePom(project)
    }

    // GitHub Package Registry
    register<MavenPublication>("gpr") {
      from(components["java"])
      configurePom(project)
    }
  }
}
