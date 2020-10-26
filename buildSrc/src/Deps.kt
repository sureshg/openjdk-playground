import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.*

/**
 * Platform versions (Global)
 */
val javaVersion = "javaVersion".systemProp.toInt()
val kotlinVersion = "KotlinVersion".systemProp
val kotlinJvmTarget = "kotlinJvmTarget".systemProp
val kotlinLangVersion = "kotlinLangVersion".systemProp
val gradleRelease = "gradleRelease".systemProp
val githubProject = "githubProject".systemProp

/**
 * Dependency versions.
 */
object Versions {
    const val micronaut = "2.0.0"
    const val jsr305 = "3.0.2"
    const val turbine = "0.1.1"
    const val commonsCodec = "1.11"
    const val rsocket = "0.11.16"
    const val failsafe = "2.3.3"
    const val typetools = "0.6.2"
    const val assertj = "3.11.1"
    const val kotlinPowerAssert = "0.3.0"
    const val truth = "1.0.1"
    const val awaitility = "3.1.6"
    const val kotest = "3.4.2"
    const val jol = "0.9"
    const val asm = "7.3.1"
    const val byteBuddy = "1.9.7"
    const val commonsIO = "2.6"
    const val jnrJffi = "1.2.18"
    const val jnrUnixSocket = "0.21"
    const val jimfs = "1.1"
    const val netty = "4.1.32.Final"
    const val eclipseCollections = "9.2.0"
    const val trov4j = "1.0.20181211"
    const val japicmp = "0.13.0"
    const val jjwt = "0.10.5"
    const val openpdf = "1.2.9"
    const val orsonpdf = "1.8"
    const val jfreesvg = "3.3"
    const val swaggerUI = "3.10.0"
    const val swaggerCodegenCli = "3.0.0"
    const val kolor = "0.0.2"
    const val ktlint = "0.39.0"
    const val reactiveStreams = "1.0.2"
    const val reactor = "3.2.5.RELEASE"
    const val ff4j = "1.3.0"
    const val jmh = "1.21"
    const val openapiGen = "4.3.1"
    const val methanol = "1.2.0"
    const val jgrapht = "1.5.0"
    const val jgitver = "0.12.0"

    // Plugins
    const val shadow = "6.1.0"
    const val micronautPlugin = "1.0.1"
    const val googleJib = "2.6.0"
    const val protobuf = "0.8.13"
    const val sonarqube = "2.7"
    const val nemerosaVersioning = "2.8.2"
    const val springboot = "2.2.6.RELEASE"
    const val springDepMgmt = "1.0.9.RELEASE"
    const val buildSrcVersions = "0.3.2"
    const val changelog = "0.4.0"
    const val spotless = "5.7.0"
    const val spotlessChangelog = "2.0.0"
    const val ktlintPlugin = "9.3.0"
    const val detekt = "1.11.0-RC1"
    const val detektCompilerPlugin = "0.3.1"
    const val spotbugs = "4.3.0"
    const val googleJavaFormat = "1.8"
    const val benmanesVersions = "0.33.0"
    const val buildScanPlugin = "2.0.2"
    const val gitPublishPlugin = "1.0.1"
    const val swaggerGen = "2.16.0"
    const val gitProperties = "2.2.4"
    const val githubRelease = "2.2.12"
    const val gradleRelease = "2.8.1"
    const val mavenRepoAuth = "3.0.4"
    const val javafxPlugin = "0.0.7"
    const val jmhPlugin = "0.4.8"
    const val mrjar = "0.0.16"
    const val jgitPlugin = "0.10.0-rc03"
    const val reckon = "0.12.0"
    const val mkdocs = "2.0.1"
    const val orchid = "0.21.1"
    const val kotless = "0.1.6"
    const val kordampGradle = "0.40.0"
    const val kordamp = "0.7.0"
}

object Deps {

    object Kotlin {
        val bom = "org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"
        val stdLib8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
        val test = "org.jetbrains.kotlin:kotlin-test:$kotlinVersion"
        val testJunit = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"

        object Coroutines {
            const val version = "1.3.9"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
            const val reactor = "org.jetbrains.kotlinx:coroutines-reactor:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            const val debug = "org.jetbrains.kotlinx:kotlinx-coroutines-debug:$version"
        }

        object Ksp {
            const val version = "1.4.10-dev-experimental-20201023"
            const val api = "com.google.devtools.ksp:symbol-processing-api:$version"
            const val ksp = "com.google.devtools.ksp:symbol-processing:$version"
            const val testing = "com.github.tschuchortdev:kotlin-compile-testing-ksp:1.2.11"
        }
    }

    object Kotlinx {
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"
        const val reflectLite = "org.jetbrains.kotlinx:kotlinx.reflect.lite:1.0.0"
        const val atomicfu = "org.jetbrains.kotlinx:atomicfu:0.14.4"
        const val io = "org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16"
        const val cli = "org.jetbrains.kotlinx:kotlinx-cli:0.3"
        const val collectionsImmutable =
            "org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.3"

        object Serialization {
            const val version = "1.0.0"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor:$version"
            const val hocon = "org.jetbrains.kotlinx:kotlinx-serialization-hocon:$version"
            const val protobuf = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$version"
            const val properties = "org.jetbrains.kotlinx:kotlinx-serialization-properties:$version"
            const val yaml = "com.charleskorn.kaml:kaml:0.17.0"
        }
    }

    object Dokka {
        const val version = "1.4.10.2"
        const val gradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:$version"
        const val javadocPlugin = "org.jetbrains.dokka:javadoc-plugin:$version"
        const val kotlinAsJavaPlugin = "org.jetbrains.dokka:kotlin-as-java-plugin:$version"
        const val gfmPlugin = "org.jetbrains.dokka:gfm-plugin:$version"
        const val jekyllPlugin = "org.jetbrains.dokka:jekyll-plugin:$version"
        const val mathjaxPlugin = "org.jetbrains.dokka:mathjax-plugin:$version"
    }

    object OpenJDK {
        const val jol = "org.openjdk.jol:jol-core:0.14"
    }

    object Jetty {
        const val version = "11.0.0.beta3"
        const val bom = "org.eclipse.jetty:jetty-bom:$version"
        const val server = "org.eclipse.jetty:jetty-server:$version"
        const val servlet = "org.eclipse.jetty:jetty-servlet:$version"
        const val util = "org.eclipse.jetty:jetty-util:$version"
        const val slf4j = "org.eclipse.jetty:jetty-slf4j-impl:$version"
        const val testHelper = "org.eclipse.jetty:jetty-test-helper:$version"
        const val servletApi = "org.eclipse.jetty.toolchain:jetty-servlet-api:4.0.4"
    }

    const val okio = "com.squareup.okio:okio:2.2.0"

    object OkHttp {
        const val version = "4.9.0"
        const val bom = "com.squareup.okhttp3:okhttp-bom:$version"
        const val okhttp = "com.squareup.okhttp3:okhttp:$version"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:$version"
        const val sse = "com.squareup.okhttp3:okhttp-sse:$version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
        const val tls = "com.squareup.okhttp3:okhttp-tls:$version"
        const val uds = "com.squareup.okhttp3.sample:unixdomainsockets:$version"
        const val doh = "com.squareup.okhttp3:okhttp-dnsoverhttps:$version"
        const val digest = "com.baulsupp:okhttp-digest:0.4.0"
        const val curl = "com.github.mrmike:ok2curl:0.4.5"
    }

    object Retrofit {
        const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val converterMoshi = "com.squareup.retrofit2:converter-moshi:$version"
        const val logging = "com.nightlynexus.logging-retrofit:logging:0.10.0"
        const val reactorAdapter = "com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0"
        const val coroutinesAdapter =
            "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
        const val koltinxSerializationAdapter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
        const val scarlet = "com.tinder.scarlet:scarlet:0.2.5"
    }

    object Moshi {
        const val version = "1.10.0"
        const val moshi = "com.squareup.moshi:moshi:$version"
        const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:$version"
        const val kotlinCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
        const val adapters = "com.squareup.moshi:moshi-adapters:$version"
    }

    object Google {
        object AutoService {
            const val version = "1.0-rc7"
            const val annotations = "com.google.auto.service:auto-service-annotations:$version"
            const val processor = "com.google.auto.service:auto-service:$version"
        }

        object Jib {
            const val core = "com.google.cloud.tools:jib-core:0.1.1"
        }
    }

    object Graal {
        const val version = "20.2.0"
        const val sdk = "org.graalvm.sdk:graal-sdk:$version"
        const val svm = "org.graalvm.nativeimage:svm:$version"
        const val js = "org.graalvm.js:js:$version"
        const val jsScriptEngine = "org.graalvm.js:js-scriptengine:$version"
        const val profiler = "org.graalvm.tools:profiler:$version"
        const val chromeinspector = "org.graalvm.tools:chromeinspector:$version"
    }

    object Cache {
        const val caffeine = "com.github.ben-manes.caffeine:caffeine:2.6.2"

        object Cache2k {
            const val version = "1.2.0.Final"
            const val api = "org.cache2k:cache2k-api:$version"
            const val core = "org.cache2k:cache2k-core:$version"
        }
    }

    object Apache {
        const val calcite = "org.apache.calcite:calcite-core:1.26.0"
    }

    object Junit {
        const val version = "5.7.0"
        const val jupiter = "org.junit.jupiter:junit-jupiter:$version"
        const val jupiterApi = "org.junit.jupiter:junit-jupiter-api:$version"
        const val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine:$version"
        const val jupiterParams = "org.junit.jupiter:junit-jupiter-params:$version"
    }

    object Mock {
        const val mockk = "io.mockk:mockk:1.10.2"
        const val mockito = "org.mockito:mockito-core:2.26.0"
        const val mockserver = "org.mock-server:mockserver-netty:5.10.0"
        const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
    }

    object Config {
        const val hoplite = "com.sksamuel.hoplite:hoplite:1.0.3"
    }

    object TemplateEngine {
        const val jte = "gg.jte:jte:1.1.0"
        const val rocker = "com.fizzed:rocker:1.3.0"
        const val stringTemplate = "org.antlr:ST4:4.3.1"
    }

    object Parser {
        const val betterParse = "com.github.h0tk3y.betterParse:better-parse:0.4.0"
        const val funcj = "org.typemeta:funcj-parser:0.6.16"
        const val antlr4 = "org.antlr:antlr4:4.8"
        const val petitparser = "com.github.petitparser:petitparser-core:2.3.1"
    }

    object Process {
        const val nuprocess = "com.zaxxer:nuprocess:2.0.1"
        const val ztexec = "org.zeroturnaround:zt-exec:1.12"
    }

    object Ktor {
        const val version = "1.4.0"
        const val bom = "io.ktor:ktor-bom:$version"
        const val serverCore = "io.ktor:ktor-server-core:$version"
        const val serverCio = "io.ktor:ktor-server-cio:$version"
    }

    object Markup {
        const val commonmark = "com.atlassian.commonmark:commonmark:0.15.1"
        const val jsoup = "org.jsoup:jsoup:1.13.1"
    }

    object CodeGen {
        const val javapoet = "com.squareup:javapoet:1.13.0"
        const val kotlinPoet = "com.squareup:kotlinpoet:1.6.0"
    }

    object Logging {

        object Slf4j {
            const val version = "1.7.30"
            const val api = "org.slf4j:slf4j-api:$version"
            const val simple = "org.slf4j:slf4j-simple:$version"
            const val nop = "org.slf4j:slf4j-nop:$version"
        }

        object Logback {
            const val classic = "ch.qos.logback:logback-classic:1.2.3"
        }
    }

    object Jackson {
        const val version = "2.11.3"
        const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
    }

    object Monitoring {
        const val beacon = "com.github.sbridges:beacon:0.9.3"
    }

    object Cli {
        const val clikt = "com.github.ajalt.clikt:clikt:3.0.1"
        const val mordant = "com.github.ajalt:mordant:1.2.1"
        const val colormath = "com.github.ajalt.colormath:colormath:2.0.0"
        const val picnic = "com.jakewharton.picnic:picnic:0.4.0"
        const val crossword = "com.jakewharton.crossword:crossword:0.1.2"
        const val jfiglet = "com.github.lalyos:jfiglet:0.0.8"

        object JLine {
            const val version = "3.9.0"
            const val jline = "org.jline:jline:$version"
            const val jlineTerminal = "org.jline:jline-terminal:$version"
            const val jlineReader = "org.jline:jline-reader:$version"
        }
    }

    object ML {

        object Tensorflow {
            const val version = "0.2.0"
            const val coreApi = "org.tensorflow:tensorflow-core-api:$version"
            const val corePlatform = "org.tensorflow:tensorflow-core-platform:$version"
            const val framework = "org.tensorflow:tensorflow-framework:$version"
        }

        object Tribuo {
            const val bom = "org.tribuo:tribuo-all:4.0.1"
        }

        object Smile {
            const val version = "2.5.0"
            const val plot = "com.github.haifengl:smile-plot:$version"
            const val core = "com.github.haifengl:smile-core:$version"
            const val vega = "com.github.haifengl:smile-scala_2.13:$version"
        }
    }

    object TLS {
        const val sslContext = "io.github.hakky54:sslcontext-kickstart:5.2.2"
        const val certifikit = "app.cash.certifikit:certifikit:0.2.0"
        const val airliftSecurity = "io.airlift:security:201"
        const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:1.60"
        const val conscryptUber = "org.conscrypt:conscrypt-openjdk-uber:1.4.1"
        const val tink = "com.google.crypto.tink:tink:1.2.1"
        const val jkeychain = "pt.davidafsilva.apple:jkeychain:1.0.0"
        const val sshj = "com.hierynomus:sshj:0.26.0"
        const val smbj = "com.hierynomus:smbj:0.9.1"
    }

    object Decompiler {
        const val jd = "org.jd:jd-core:1.1.3"
        const val cfr = "org.benf:cfr:0.150"
    }

    object Maven {
        const val shrinkwrap = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4"
    }

    object OS {
        const val oshi = "com.github.oshi:oshi-core:5.2.5"
    }

    object Encoding {
        const val asn1 = "com.hierynomus:asn-one:0.1.0"
        const val asn1bean = "com.beanit:asn1bean:1.12.0"
    }

    object Compression {
        const val jvmbrotli = "com.nixxcode.jvmbrotli:jvmbrotli:0.2.0"
        const val brotli = "org.brotli:dec:0.1.2"
    }

    object Reflection {
        const val jandex = "org.jboss:jandex:2.2.1.Final"
    }

    const val micronautBom = "io.micronaut:micronaut-bom:${Versions.micronaut}"
    const val methanol = "com.github.mizosoft.methanol:methanol:${Versions.methanol}"

    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    const val failsafe = "net.jodah:failsafe:${Versions.failsafe}"
    const val typetools = "net.jodah:typetools:${Versions.typetools}"
    const val nettyResolveDns = "io.netty:netty-resolver-dns:${Versions.netty}"
    const val jffi = "com.github.jnr:jffi:${Versions.jnrJffi}"
    const val jnrUnixSocket = "com.github.jnr:jnr-unixsocket:${Versions.jnrUnixSocket}"
    const val commonsIO = "commons-io:commons-io:${Versions.commonsIO}"
    const val commonsCodec = "commons-codec:commons-codec:${Versions.commonsCodec}"
    const val jjwt = "io.jsonwebtoken:jjwt-api:${Versions.jjwt}"
    const val jjwtImpl = "io.jsonwebtoken:jjwt-impl:${Versions.jjwt}"
    const val jjwtJackson = "io.jsonwebtoken:jjwt-jackson:${Versions.jjwt}"
    const val jfreesvg = "org.jfree:jfreesvg:${Versions.jfreesvg}"
    const val orsonpdf = "com.orsonpdf:orsonpdf:${Versions.orsonpdf}"
    const val openpdf = "com.github.librepdf:openpdf:${Versions.openpdf}"
    const val jimfs = "com.google.jimfs:jimfs:${Versions.jimfs}"
    const val jsr305 = "com.google.code.findbugs:jsr305:${Versions.jsr305}"
    const val jgrapht = "org.jgrapht:jgrapht-core:${Versions.jgrapht}"
    const val jsonPath = "com.jayway.jsonpath:json-path:2.4.0"

    const val funcj = "org.typemeta:funcj:0.6.16"
    const val asm = "org.ow2.asm:asm:${Versions.asm}"
    const val asmUtil = "org.ow2.asm:asm-util:${Versions.asm}"
    const val byteBuddy = "net.bytebuddy:byte-buddy:${Versions.byteBuddy}"
    const val jol = "org.openjdk.jol:jol-core:${Versions.jol}"
    const val japicmp = "com.github.siom79.japicmp:japicmp:${Versions.japicmp}"
    const val kolor = "com.andreapivetta.kolor:kolor:${Versions.kolor}"

    const val reactiveStreams = "org.reactivestreams:reactive-streams:${Versions.reactiveStreams}"
    const val reactiveStreamsFlowAdapters =
        "org.reactivestreams:reactive-streams-flow-adapters:${Versions.reactiveStreams}"
    const val reactorCore = "io.projectreactor:reactor-core:${Versions.reactor}"
    const val reactorTest = "io.projectreactor:reactor-test:${Versions.reactor}"

    const val trov4j = "org.jetbrains.intellij.deps:trove4j:${Versions.trov4j}"
    const val ff4j = "org.ff4j:ff4j-core:${Versions.ff4j}"
    const val jmhCore = "org.openjdk.jmh:jmh-core:${Versions.jmh}"
    const val jmhGenAnnprocess = "org.openjdk.jmh:jmh-generator-annprocess:${Versions.jmh}"

    const val rsocketCore = "io.rsocket:rsocket-core:${Versions.rsocket}"
    const val rsocketNetty = "io.rsocket:rsocket-transport-netty:${Versions.rsocket}"

    const val openapiGen = "org.openapitools:openapi-generator:${Versions.openapiGen}"
    const val swaggerUI = "org.webjars:swagger-ui:${Versions.swaggerUI}"
    const val swaggerCodegenCli =
        "io.swagger.codegen.v3:swagger-codegen-cli:${Versions.swaggerCodegenCli}"

    const val jgitver = "fr.brouillard.oss:jgitver:${Versions.jgitver}"

    const val assertjCore = "org.assertj:assertj-core:${Versions.assertj}"
    const val kotlinPowerAssert =
        "com.bnorm.power:kotlin-power-assert:${Versions.kotlinPowerAssert}"
    const val googleTruth = "com.google.truth:truth:${Versions.truth}"

    const val awaitility = "org.awaitility:awaitility:${Versions.awaitility}"
    const val awaitilityKotlin = "org.awaitility:awaitility-kotlin:${Versions.awaitility}"

    const val kotestAssertions = "io.kotlintest:kotlintest-assertions:${Versions.kotest}"
    const val kotestJUnit5Runner = "io.kotest:kotest-runner-junit5-jvm:${Versions.kotest}"
}

/**
 * Returns the system properties value of the given string.
 */
val String.systemProp: String get() = System.getProperty(this, "")

/**
 * PluginId Extensions
 */
inline val PluginDependenciesSpec.kotlinJvm get() = kotlin("jvm") version kotlinVersion
inline val PluginDependenciesSpec.kotlinxSerialization get() = kotlin("plugin.serialization") version kotlinVersion
inline val PluginDependenciesSpec.kotlinKapt get() = kotlin("kapt") version kotlinVersion
inline val PluginDependenciesSpec.kotlinSpring get() = kotlin("plugin.spring") version kotlinVersion
inline val PluginDependenciesSpec.kotlinAllOpen get() = kotlin("plugin.allopen") version kotlinVersion
inline val PluginDependenciesSpec.kotlinNoArg get() = kotlin("plugin.noarg") version kotlinVersion
inline val PluginDependenciesSpec.kotlinJpa get() = kotlin("plugin.jpa") version kotlinVersion
inline val PluginDependenciesSpec.kotlinScript get() = kotlin("plugin.scripting") version kotlinVersion
inline val PluginDependenciesSpec.dokka get() = id("org.jetbrains.dokka") version Deps.Dokka.version
inline val PluginDependenciesSpec.ksp get() = id("symbol-processing") version Deps.Kotlin.Ksp.version
inline val PluginDependenciesSpec.changelog get() = id("org.jetbrains.changelog") version Versions.changelog
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow") version Versions.shadow
inline val PluginDependenciesSpec.protobuf get() = id("com.google.protobuf") version Versions.protobuf
inline val PluginDependenciesSpec.benmanesVersions get() = id("com.github.ben-manes.versions") version Versions.benmanesVersions
inline val PluginDependenciesSpec.gitProperties get() = id("com.gorylenko.gradle-git-properties") version Versions.gitProperties
inline val PluginDependenciesSpec.spotless get() = id("com.diffplug.spotless") version Versions.spotless
inline val PluginDependenciesSpec.spotlessChangelog get() = id("com.diffplug.spotless-changelog") version Versions.spotlessChangelog
inline val PluginDependenciesSpec.spotbugs get() = id("com.github.spotbugs") version Versions.spotbugs
inline val PluginDependenciesSpec.ktlint get() = id("com.eden.orchidPlugin") version Versions.ktlintPlugin
inline val PluginDependenciesSpec.detekt get() = id("io.gitlab.arturbosch.detekt") version Versions.detekt
inline val PluginDependenciesSpec.detektCompilerPlugin get() = id("io.github.detekt.gradle.compiler-plugin") version Versions.detektCompilerPlugin
inline val PluginDependenciesSpec.githubRelease get() = id("com.github.breadmoirai.github-release") version Versions.githubRelease
inline val PluginDependenciesSpec.gradleRelease get() = id("net.researchgate.release") version Versions.gradleRelease
inline val PluginDependenciesSpec.mavenRepoAuth get() = id("org.hibernate.build.maven-repo-auth") version Versions.mavenRepoAuth
inline val PluginDependenciesSpec.javafx get() = id("org.openjfx.javafxplugin") version Versions.javafxPlugin
inline val PluginDependenciesSpec.jmh get() = id("me.champeau.gradle.jmh") version Versions.jmhPlugin
inline val PluginDependenciesSpec.mrjar get() = id("com.lingocoder.mrjar") version Versions.mrjar
inline val PluginDependenciesSpec.googleJib get() = id("com.google.cloud.tools.jib") version Versions.googleJib
inline val PluginDependenciesSpec.springboot get() = id("org.springframework.boot") version Versions.springboot
inline val PluginDependenciesSpec.springDepMgmt get() = id("io.spring.dependency-management") version Versions.springDepMgmt
inline val PluginDependenciesSpec.micronautApplication get() = id("io.micronaut.application") version Versions.micronautPlugin
inline val PluginDependenciesSpec.micronautLibrary get() = id("io.micronaut.library") version Versions.micronautPlugin
inline val PluginDependenciesSpec.jgitPlugin get() = id("fr.brouillard.oss.gradle.jgitver") version Versions.jgitPlugin
inline val PluginDependenciesSpec.reckon get() = id("org.ajoberstar.reckon") version Versions.reckon
inline val PluginDependenciesSpec.mkdocs get() = id("ru.vyarus.mkdocs") version Versions.mkdocs
inline val PluginDependenciesSpec.orchid get() = id("com.eden.orchidPlugin") version Versions.orchid
inline val PluginDependenciesSpec.kotless get() = id("io.kotless") version Versions.kotless
inline val PluginDependenciesSpec.kordampGradle get() = id("org.kordamp.gradle.project") version Versions.kordampGradle
inline val PluginDependenciesSpec.gradleEnforcer get() = id("org.kordamp.gradle.project-enforcer") version Versions.kordamp
inline val PluginDependenciesSpec.jandex get() = id("org.kordamp.gradle.jandex") version Versions.kordamp
