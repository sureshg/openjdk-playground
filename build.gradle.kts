import com.google.cloud.tools.jib.plugins.common.ContainerizingMode
import dev.suresh.gradle.addModules
import dev.suresh.gradle.javaRelease
import dev.suresh.gradle.javaVersion
import dev.suresh.gradle.joinToConfigString
import dev.suresh.gradle.kotlinJvmTarget
import dev.suresh.gradle.tmp
import java.net.URI
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
  id("plugins.common")
  id("plugins.misc")
  id("plugins.publishing")
  id("plugins.kotlin")
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.ksp.redacted)
  alias(libs.plugins.ksp.powerassert)
  alias(libs.plugins.javaagent.application)
  alias(libs.plugins.google.jib)
  alias(libs.plugins.jetbrains.qodana)
  alias(libs.plugins.sonarqube)
  alias(libs.plugins.spotless.changelog)
  alias(libs.plugins.gradle.checksum)
  alias(libs.plugins.kotlinx.bincompat)
  alias(libs.plugins.gradlex.javamoduleinfo)
  alias(libs.plugins.buildkonfig) apply false
  alias(libs.plugins.licensee) apply false
}

application {
  mainClass = libs.versions.app.mainclass
  // mainModule = libs.versions.app.module
  applicationDefaultJvmArgs +=
      listOf(
          "--show-version",
          "--enable-preview",
          "--add-modules=$addModules",
          "--enable-native-access=ALL-UNNAMED",
          "-XshowSettings:vm",
          "-XshowSettings:system",
          "-XshowSettings:properties",
          "-Xmx96M",
          "-XX:+PrintCommandLineFlags",
          "-XX:+UseZGC",
          "-XX:+ZGenerational",
          "-XX:+UnlockExperimentalVMOptions",
          "-XX:LockingMode=2", // New experimental lightweight locking
          // "-XX:+UseCompactObjectHeaders",
          // os+thread,gc+heap=trace,
          """-Xlog:cds,safepoint,gc*:
              |file="$tmp$name-gc-%p-%t.log":
              |level,tags,time,uptime,pid,tid:
              |filecount=5,
              |filesize=10m"""
              .joinToConfigString(),
          """-XX:StartFlightRecording=
              |filename=$tmp$name.jfr,
              |name=$name,
              |maxsize=100M,
              |maxage=1d,
              |path-to-gc-roots=true,
              |dumponexit=true,
              |memory-leaks=gc-roots,
              |gc=detailed,
              |+jdk.VirtualThreadStart#enabled=true,
              |+jdk.VirtualThreadEnd#enabled=true,
              |jdk.ObjectCount#enabled=true,
              |jdk.SecurityPropertyModification#enabled=true,
              |jdk.TLSHandshake#enabled=true,
              |jdk.X509Certificate#enabled=true,
              |jdk.X509Validation#enabled=true,
              |settings=profile"""
              .joinToConfigString(),
          "-XX:FlightRecorderOptions:stackdepth=64",
          "-XX:+HeapDumpOnOutOfMemoryError",
          "-XX:HeapDumpPath=$tmp$name-%p.hprof",
          "-XX:ErrorFile=$tmp$name-hs-err-%p.log",
          "-XX:OnOutOfMemoryError='kill -9 %p'",
          "-XX:+ExitOnOutOfMemoryError",
          "-XX:+UnlockDiagnosticVMOptions",
          // "-XX:NativeMemoryTracking=detail",
          "-XX:+EnableDynamicAgentLoading",
          "-XX:+LogVMOutput",
          "-XX:LogFile=$tmp$name-jvm.log",
          "-XX:+ShowHiddenFrames",
          "-Djava.awt.headless=true",
          "-Djdk.attach.allowAttachSelf=true",
          "-Djdk.traceVirtualThreadLocals=false",
          "-Djdk.tracePinnedThreads=full",
          "-Djava.security.debug=properties",
          "-Djava.security.egd=file:/dev/./urandom",
          "-Djdk.includeInExceptions=hostInfo,jar",
          "-Dkotlinx.coroutines.debug",
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
          // "-XX:NativeMemoryTracking=[off|summary|detail]",
          // "-XX:+PrintNMTStatistics",
          // "-XX:OnError=\"gdb - %p\"", // Attach gdb on segfault
          // "-Djava.security.properties=/path/to/custom/java.security", // == to override
          // "-Duser.timezone=\"PST8PDT\"",
          // "-Djava.net.preferIPv4Stack=true",
          // "-Djavax.net.debug=all",
          // "-Dhttps.protocols=TLSv1.2",
          // "-Dhttps.agent=$name",
          // "-Dhttp.keepAlive=true",
          // "-Dhttp.maxConnections=5",
          // "-Djava.security.manager=allow",
          // "-Dfile.encoding=COMPAT", // uses '-Dnative.encoding'
          // "-Djdbc.drivers=org.postgresql.Driver",
          // "-Djava.io.tmpdir=/var/data/tmp",
          // "-Djava.locale.providers=COMPAT,CLDR",
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
  // $ java -XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal
  // --version
  // https://docs.oracle.com/en/java/javase/21/docs/specs/man/java.html
  // https://docs.oracle.com/en/java/javase/21/core/java-networking.html#GUID-E6C82625-7C02-4AB3-B15D-0DF8A249CD73
  // https://cs.oswego.edu/dl/jsr166/dist/jsr166.jar
  // https://chriswhocodes.com/hotspot_options_openjdk21.html
  // https://sap.github.io/SapMachine/jfrevents/21.html
}

ksp {
  arg("autoserviceKsp.verify", "true")
  arg("autoserviceKsp.verbose", "true")
  // arg(KspArgsProvider(project.layout.projectDirectory.file("config").asFile))
}

redacted { enabled = true }

apiValidation { validationDisabled = true }

qodana { autoUpdate = true }

kotlinPowerAssert { functions = listOf("kotlin.assert", "kotlin.test.assertTrue") }

sonarqube {
  properties {
    properties["sonar.projectKey"] = "sureshg_openjdk-playground"
    properties["sonar.organization"] = "sureshg"
    properties["sonar.host.url"] = "https://sonarcloud.io"
  }
}

jib {
  from {
    image = "openjdk:$javaVersion-slim"
    platforms {
      platform {
        architecture = "arm64"
        os = "linux"
      }
      platform {
        architecture = "amd64"
        os = "linux"
      }
    }
  }

  to {
    image = "sureshg/${project.name}"
    tags = setOf(project.version.toString(), "latest")
  }

  container {
    mainClass = application.mainClass.get()
    jvmFlags = application.applicationDefaultJvmArgs.toList()
    environment = mapOf("Author" to "Suresh")
  }

  containerizingMode = ContainerizingMode.PACKAGED.toString()
}

jdeprscan { forRemoval = true }

// Create ShadowJar specific runtimeClasspath.
val shadowRuntime: Configuration by
    configurations.creating {
      val runtimeClasspath by configurations.getting
      extendsFrom(runtimeClasspath)
      attributes { attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME)) }
    }

// Deactivate java-module-info plugin for all configs
configurations {
  // runtimeClasspath...etc
  all { attributes { attribute(Attribute.of("javaModule", Boolean::class.javaObjectType), false) } }
}

tasks {
  run.invoke { args(true) }

  // Javadoc
  javadoc {
    isFailOnError = true
    modularity.inferModulePath = true
    (options as StandardJavadocDocletOptions).apply {
      encoding = "UTF-8"
      linkSource(true)
      addBooleanOption("-enable-preview", true)
      addStringOption("-add-modules", addModules)
      addStringOption("-release", javaRelease.get().toString())
      addStringOption("Xdoclint:none", "-quiet")
    }
  }

  // Dokka config
  withType<DokkaTask>().configureEach {
    outputDirectory = buildDir.resolve("dokka")
    moduleName = project.name

    dokkaSourceSets {
      configureEach {
        displayName = "JVM"
        includes.from("README.md")
        jdkVersion = kotlinJvmTarget.map { it.target.toInt() }
        noStdlibLink = false
        noJdkLink = false
        // sourceRoots.setFrom(file("src/main/kotlin"))

        sourceLink {
          localDirectory = file("src/main/kotlin")
          remoteUrl = URI("${libs.versions.githubProject.get()}/tree/main/src/main/kotlin").toURL()
          remoteLineSuffix = "#L"
        }

        externalDocumentationLink {
          url = URI("https://kotlin.github.io/kotlinx.coroutines/package-list").toURL()
        }

        perPackageOption {
          matchingRegex = "kotlin($|\\.).*"
          skipDeprecated = false
          reportUndocumented = true // Emit warnings about not documented members
          includeNonPublic = false
        }
      }
    }
  }

  // Uber jar
  shadowJar {
    archiveClassifier = "uber"
    description = "Create a fat JAR of $archiveFileName and runtime dependencies."
    mergeServiceFiles()

    // Don't create modular shadow jar
    // exclude("module-info.class")
    // relocate("okio", "shaded.okio")
    // configurations = listOf(shadowRuntime)
  }
}

dependencies {
  implementation(platform(libs.okhttp.bom))
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.datetime)
  implementation(libs.jetty.server) { version { strictly(libs.versions.jetty.asProvider().get()) } }
  implementation(libs.jetty.ee10.servlet)
  implementation(libs.slf4j.api)
  implementation(libs.slf4j.simple)
  implementation(libs.jackson.databind)
  implementation(libs.jte.runtime)
  implementation(libs.jmdns)
  implementation(libs.password4j) { exclude(group = "org.slf4j", module = "slf4j-nop") }
  implementation(libs.certifikit)
  implementation(libs.otp.java)
  implementation(libs.urlbuilder)
  implementation(libs.okhttp)
  implementation(libs.okhttp.mockwebserver)
  implementation(libs.okhttp.tls)
  implementation(libs.okhttp.logginginterceptor)
  implementation(libs.kotlinRetry)
  implementation(libs.jakewharton.crossword)
  implementation(libs.ajalt.clikt)
  implementation(libs.ajalt.mordant)
  implementation(libs.log4j.core)
  implementation(libs.jspecify)
  implementation(libs.sourceBuddy)
  implementation(libs.reflect.typeparamresolver)
  implementation(libs.maven.archeologist)

  compileOnly(libs.jte.kotlin)
  compileOnly(libs.kotlinx.atomicfu)

  // Auto-service
  ksp(libs.ksp.auto.service)
  implementation(libs.google.auto.annotations)
  // kapt("com.google.auto.service:auto-service:1.0.1")

  // api(platform(projects.playgroundBom))
  // api(projects.playgroundCatalog)
  // api(project(":jdk-modules:ffm-api"))

  // Included builds
  implementation(libs.module.ffm.api)
  javaagent(libs.module.jvm.agent)
  // javaagent(libs.glowroot.agent)

  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.pioneer)
  testImplementation(kotlin("test-junit5"))
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.lincheck)
  testImplementation(libs.kotest.core)
  testImplementation(libs.kotest.junit5)
  testImplementation(libs.slf4j.simple)
  testImplementation(libs.mockk)

  // Dokka Plugins (dokkaHtmlPlugin, dokkaGfmPlugin)
  dokkaPlugin(libs.dokka.mermaid)

  subprojects.forEach { kover(it) }

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
