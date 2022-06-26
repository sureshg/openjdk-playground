import org.gradle.api.artifacts.dsl.*
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.*

/** Platform versions defined as System Properties. */
val javaVersion by sysProp<Int>()
val kotlinVersion by sysProp<String>()
val kotlinJvmTarget by sysProp<String>()
val kotlinApiVersion by sysProp<String>()
val kotlinLangVersion by sysProp<String>()
val gradleRelease by sysProp<String>()
val ktlintVersion by sysProp<String>()
val gjfVersion by sysProp<String>()
val githubProject by sysProp<String>()
val jvmArguments by sysProp<List<String>>()
val addModules by sysProp<String>()

/** Dependency versions. */
object Deps {

    object Jdk {

        object Jmc

        object Gc {
            const val toolkit = "com.microsoft.gctoolkit:api:2.0.1"
        }

        object Javadoc {
            const val link = "io.javaalmanac:javadoclink:1.0.0"
        }
    }

    object Kotlin {
        val bom = "org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"
        val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
        val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
        val test = "org.jetbrains.kotlin:kotlin-test:$kotlinVersion"
        val testJunit = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
        val compiler = "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"
        val libsPubVersion = "0.0.60-dev-32"

        object Script {
            val runtime = "org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion"
            val util = "org.jetbrains.kotlin:kotlin-script-util:$kotlinVersion"
            val jsr223 = "org.jetbrains.kotlin:kotlin-scripting-jsr223:$kotlinVersion"
            val mainKts = "org.jetbrains.kotlin:kotlin-main-kts:$kotlinVersion"
        }

        object Coroutines {
            const val version = "1.6.3"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val jdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$version"
            const val reactor = "org.jetbrains.kotlinx:coroutines-reactor:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            const val debug = "org.jetbrains.kotlinx:kotlinx-coroutines-debug:$version"
        }

        object Ksp {
            const val version = "1.7.0-1.0.6"
            const val api = "com.google.devtools.ksp:symbol-processing-api:$version"
            const val ksp = "com.google.devtools.ksp:symbol-processing:$version"
            const val testing = "com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.0"
        }

        object Reflekt {
            const val version = "1.5.30"
            const val xx = "io.reflekt:reflekt-dsl:$version"
        }

        object Data {
            const val multik = "org.jetbrains.kotlinx:multik-api:0.0.1"
            const val deeplearning = "org.jetbrains.kotlin-deeplearning:api:0.1.1"
            const val letsPlotKotlin = "org.jetbrains.lets-plot-kotlin:lets-plot-kotlin-api:1.2.0"
            const val sparkApi = "org.jetbrains.kotlinx.spark:kotlin-spark-api-3.0:1.0.0-preview2"
        }

        const val metadata = "me.eugeniomarletti.kotlin.metadata:kotlin-metadata:1.4.0"
        const val metadataJvm = "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.1.0"
        const val compileTesting = "com.github.tschuchortdev:kotlin-compile-testing:1.4.0"
        const val marketplaceZipSigner = "org.jetbrains:marketplace-zip-signer:0.1.3"
        const val markdown = "org.jetbrains:markdown:0.2.1"
        const val lincheck = "org.jetbrains.kotlinx:lincheck:2.12"
    }

    object KotlinJS {
        const val kvision = "io.kvision:kvision:4.0.0"
        const val kotlinCss = "org.jetbrains:kotlin-css:1.0.0"
    }

    object Kotlinx {
        const val atomicfuVersion = "0.18.0"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
        const val reflectLite = "org.jetbrains.kotlinx:kotlinx.reflect.lite:1.0.0"
        const val atomicfu = "org.jetbrains.kotlinx:atomicfu:$atomicfuVersion"
        const val io = "org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16"
        const val cli = "org.jetbrains.kotlinx:kotlinx-cli:0.3"
        const val collectionsImmutable =
            "org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4"

        object Serialization {
            const val version = "1.3.3"
            const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:$version"
            const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            const val cbor = "org.jetbrains.kotlinx:kotlinx-serialization-cbor:$version"
            const val hocon = "org.jetbrains.kotlinx:kotlinx-serialization-hocon:$version"
            const val protobuf = "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$version"
            const val properties = "org.jetbrains.kotlinx:kotlinx-serialization-properties:$version"
            const val yaml = "com.charleskorn.kaml:kaml:0.17.0"
            const val csv = "de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:2.0.0"
        }
    }

    object Grpc {
        const val version = "1.1.0"
        const val kotlinStub = "io.grpc:grpc-kotlin-stub:$version"
        const val protocGenKotlin = "io.grpc:protoc-gen-grpc-kotlin:$version"
    }

    object Record {
        const val builder = "io.soabase.record-builder:record-builder:1.19"
    }

    object Exposed {
        const val version = "0.28.1"
        const val core = "org.jetbrains.exposed:exposed-core:$version"
        const val dao = "org.jetbrains.exposed:exposed-dao:$version"
        const val jdbc = "org.jetbrains.exposed:exposed-jdbc:$version"
        const val opentracing = "com.github.fstien:exposed-opentracing:0.2.1"
    }

    object Dokka {
        const val version = "1.7.0"
        const val gradlePlugin = "org.jetbrains.dokka:dokka-gradle-plugin:$version"
        const val javadocPlugin = "org.jetbrains.dokka:javadoc-plugin:$version"
        const val kotlinAsJavaPlugin = "org.jetbrains.dokka:kotlin-as-java-plugin:$version"
        const val gfmPlugin = "org.jetbrains.dokka:gfm-plugin:$version"
        const val jekyllPlugin = "org.jetbrains.dokka:jekyll-plugin:$version"
        const val mathjaxPlugin = "org.jetbrains.dokka:mathjax-plugin:$version"
        const val mermaidPlugin = "com.glureau:html-mermaid-dokka-plugin:0.3.2"
    }

    object OpenJDK {
        const val jol = "org.openjdk.jol:jol-core:0.14"
        const val nashorn = "org.openjdk.nashorn:nashorn-core:15.0"
    }

    object Foojay {
        const val discoclient = "io.foojay.api:discoclient:2.0.21"
    }

    object Jetty {
        const val version = "11.0.11"
        const val bom = "org.eclipse.jetty:jetty-bom:$version"
        const val server = "org.eclipse.jetty:jetty-server:$version"
        const val servlet = "org.eclipse.jetty:jetty-servlet:$version"
        const val servlets =
            "org.eclipse.jetty:jetty-servlets:$version" // Utility Servlets and Filters
        const val util = "org.eclipse.jetty:jetty-util:$version"
        const val slf4j = "org.eclipse.jetty:jetty-slf4j-impl:$version"
        const val testHelper = "org.eclipse.jetty:jetty-test-helper:$version"
        const val jakartaServletApi = "org.eclipse.jetty.toolchain:jetty-jakarta-servlet-api:5.0.2"

        object LoadGen {
            const val version = "3.1.2"
            const val client =
                "org.mortbay.jetty.loadgenerator:jetty-load-generator-client:$version"
            const val listebers =
                "org.mortbay.jetty.loadgenerator:jetty-load-generator-listeners:$version"
        }
    }

    // Java 11 HTTP addons
    object Http {
        const val methanol = "com.github.mizosoft.methanol:methanol:1.4.1"
        const val urlbuilder = "io.mikael:urlbuilder:2.0.9"
        const val urlDetector = "com.linkedin.urls:url-detector:0.1.17"
        const val interceptor = "codes.rafael.interceptablehttpclient:interceptable-http-client:1.0"
    }

    object Okio {
        const val version = "3.0.0"
        const val bom = "com.squareup.okio:okio-bom:$version"
        const val io = "com.squareup.okio:okio"
        const val fakefilesystem = "com.squareup.okio:okio-fakefilesystem"
    }

    object OkHttp {
        const val version = "5.0.0-alpha.9"
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
        const val eithernet = "com.slack.eithernet:eithernet:1.0.0-rc01"
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
            const val version = "1.0.1"
            const val annotations = "com.google.auto.service:auto-service-annotations:$version"
            const val processor = "com.google.auto.service:auto-service:$version"
        }

        object Jib {
            const val core = "com.google.cloud.tools:jib-core:0.1.1"
        }

        object ApiService {
            const val sdmv1 =
                "com.google.apis:google-api-services-smartdevicemanagement:v1-rev20210604-1.32.1"
        }

        const val re2j = "com.google.re2j:re2j:1.6"
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

    object Junit {
        const val version = "5.9.0-M1"
        const val bom = "org.junit:junit-bom:$version"
        const val jupiter = "org.junit.jupiter:junit-jupiter"
        const val jupiterApi = "org.junit.jupiter:junit-jupiter-api"
        const val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine"
        const val jupiterParams = "org.junit.jupiter:junit-jupiter-params"
        const val pioneer = "org.junit-pioneer:junit-pioneer:1.7.1"
    }

    object KoTest {
        const val version = "5.3.2"
        const val junit5Runner = "io.kotest:kotest-runner-junit5:$version"
        const val assertions = "io.kotest:kotest-assertions-core:$version"
        const val property = "io.kotest:kotest-property:$version"
        const val datetimeAssertions =
            "io.kotest.extensions:kotest-assertions-kotlinx-datetime:1.0.0"
        const val ktorAssertions = "io.kotest.extensions:kotest-assertions-ktor:1.0.1"
        const val testcontainers = "io.kotest.extensions:kotest-extensions-testcontainers:1.0.0"
    }

    object Mock {
        const val mockk = "io.mockk:mockk:1.12.4"
        const val mockito = "org.mockito:mockito-core:2.26.0"
        const val mockserver = "org.mock-server:mockserver-netty:5.10.0"
        const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
    }

    object Config {
        const val hoplite = "com.sksamuel.hoplite:hoplite:1.0.3"
    }

    object TemplateEngine {
        const val rocker = "com.fizzed:rocker:1.3.0"
        const val stringTemplate = "org.antlr:ST4:4.3.1"

        object Jte {
            const val version = "2.1.1"
            const val jte = "gg.jte:jte:$version"
            const val runtime = "gg.jte:jte-runtime:$version"
            const val kotlin = "gg.jte:jte-kotlin:$version"
        }
    }

    object Parser {
        const val betterParse = "com.github.h0tk3y.betterParse:better-parse:0.4.1"
        const val petitparser = "com.github.petitparser:petitparser-core:2.3.1"

        const val antlr4 = "org.antlr:antlr4:4.8"
        const val funcj = "org.typemeta:funcj-parser:0.6.16"

        const val javaparserCore = "com.github.javaparser:javaparser-core:3.18.0"
        const val javaparserSSCore = "com.github.javaparser:javaparser-symbol-solver-core:3.18.0"

        const val jflex = "de.jflex:jflex:1.8.2"
        const val javacc = "net.java.dev.javacc:javacc:7.0.10"
        const val jparsec = "org.jparsec:jparsec:3.1"
    }

    object LangTools {
        const val version = "5.2"
        const val en = "org.languagetool:language-en:$version"
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

    object Process {
        const val nuprocess = "com.zaxxer:nuprocess:2.0.1"
        const val ztexec = "org.zeroturnaround:zt-exec:1.12"
        const val kotlinProcess = "com.github.pgreze:kotlin-process:1.2"
    }

    object Ktor {
        const val version = "1.5.3"
        const val bom = "io.ktor:ktor-bom:$version"
        const val serverCore = "io.ktor:ktor-server-core:$version"
        const val serverCio = "io.ktor:ktor-server-cio:$version"
        const val retrofit = "com.bnorm.ktor.retrofit:ktor-retrofit:0.1.2"
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
            const val version = "2.0.0-alpha7"
            const val api = "org.slf4j:slf4j-api:$version"
            const val nop = "org.slf4j:slf4j-nop:$version"
            const val simple = "org.slf4j:slf4j-simple:$version"
            const val jdk14 = "org.slf4j:slf4j-jdk14:$version"
        }

        object Logback {
            const val classic = "ch.qos.logback:logback-classic:1.2.8"
        }

        object Log4j2 {
            const val version = "2.17.2"
            const val bom = "org.apache.logging.log4j:log4j-bom:$version"
            const val core = "org.apache.logging.log4j:log4j-core:$version"
        }

        object Zerolog {
            const val core = "com.obsidiandynamics.zerolog:zerolog-core:0.30.0"
        }
    }

    object Jackson {
        const val version = "2.13.3"
        const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
    }

    object Monitoring {
        const val beacon = "com.github.sbridges:beacon:0.9.3"
    }

    object Cli {
        const val clikt = "com.github.ajalt.clikt:clikt:3.5.0"
        const val textIO = "org.beryx:text-io:3.4.1"
        const val mordant = "com.github.ajalt.mordant:mordant:2.0.0-beta7"
        const val colormath = "com.github.ajalt.colormath:colormath:2.0.0"
        const val mosaic = "com.jakewharton.mosaic:1.0.0"
        const val picnic = "com.jakewharton.picnic:picnic:0.6.0"
        const val crossword = "com.jakewharton.crossword:crossword:0.2.0"
        const val progressbar = "me.tongfei:progressbar:0.9.0"
        const val progressKt = "com.importre:progress:0.1.0"
        const val textTree = "org.barfuin.texttree:text-tree:2.1.1"

        const val lanterna = "com.googlecode.lanterna:lanterna:3.2.0-alpha1"
        const val jexerTui = "com.gitlab.klamonte:jexer:0.3.2"
        const val asciiGraph = "com.mitchtalmadge:ascii-data:1.4.0"
        const val barcodes = "com.github.walleth.console-barcodes:lib:0.2"
        // https://github.com/topics/ascii-art?l=java
        // https://github.com/willmcgugan/rich (Python Lib for Rich Text)

        const val jfiglet = "com.github.lalyos:jfiglet:0.0.8"
        const val bananaFiglet = "io.leego:banana:2.0.1"

        object JLine {
            const val version = "3.9.0"
            const val jline = "org.jline:jline:$version"
            const val jlineTerminal = "org.jline:jline-terminal:$version"
            const val jlineReader = "org.jline:jline-reader:$version"
        }

        object PicoCli {
            const val version = "4.6.1"
            const val core = "info.picocli:picocli:$version"
            const val codeGenAnn = "info.picocli:picocli-codegen:$version"
        }
    }

    object Viz {
        const val graphvizJava = "guru.nidi:graphviz-java:0.18.1"
        const val graphvizKotlin = "guru.nidi:graphviz-kotlin:0.18.1"
    }

    object TLS {
        const val sslContext = "io.github.hakky54:sslcontext-kickstart:5.2.2"
        const val certifikit = "app.cash.certifikit:certifikit:0.2.0"
        const val airliftSecurity = "io.airlift:security:201"
        const val churchkey = "org.tomitribe:churchkey:0.15"
        const val bruce = "com.github.mcaserta:bruce:1.0.3"
        const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:1.60"
        const val conscryptUber = "org.conscrypt:conscrypt-openjdk-uber:1.4.1"
        const val tink = "com.google.crypto.tink:tink:1.2.1"
        const val acme4j = "org.shredzone.acme4j:acme4j:2.11"
    }

    object Security {
        const val password4j = "com.password4j:password4j:1.6.0"
        const val otp = "com.github.bastiaanjansen:otp-java:1.3.2"
        const val totp = "dev.samstevens.totp:totp:1.7.1"
        const val jwtJava = "com.github.bastiaanjansen:jwt-java:1.2.0"
        const val twoFactorAuth = "com.j256.two-factor-auth:two-factor-auth:1.3"
        const val zxingCore = "com.google.zxing:core:3.4.1"
        const val sshj = "com.hierynomus:sshj:0.26.0"
        const val smbj = "com.hierynomus:smbj:0.9.1"
        const val jarSign = "org.simplify4u.plugins:sign-maven-plugin:0.3.0"
        const val jkeychain = "pt.davidafsilva.apple:jkeychain:1.0.0"
    }

    object Network {
        const val dnsJava = "dnsjava:dnsjava:3.3.1"
        const val jmdns = "org.jmdns:jmdns:3.5.7"
        const val reverseCountryCode = "uk.recurse:reverse-country-code:1.0.0"
        const val microRaft = "com.github.MicroRaft:MicroRaft:v0.1"
    }

    object Tcp {
        const val simpleNet = "com.github.jhg023:SimpleNet:1.6.6"
        const val oneNio = "ru.odnoklassniki:one-nio:1.4.0"
    }

    object Retry {
        const val failsafe = "net.jodah:failsafe:2.4.0"
        const val kotlinRetry = "com.michael-bull.kotlin-retry:kotlin-retry:1.0.9"
        const val bucket4j = "com.github.vladimir-bukhtoyarov:bucket4j-core:6.4.1"
        const val resilience4jRetry = "io.github.resilience4j:resilience4j-retry:1.6.1"
    }

    object Decompiler {
        const val cfr = "org.benf:cfr:0.151"
        const val jdCore = "org.jd:jd-core:1.1.3"
        const val procyonCore = "org.bitbucket.mstrobel:procyon-core:0.5.36"

        // http://files.minecraftforge.net/maven
        const val fernflower = "net.minecraftforge:fernflower:402"
    }

    object Maven {
        const val shrinkwrap = "org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-depchain:3.1.4"
    }

    object Git {
        const val jgit = "org.eclipse.jgit:org.eclipse.jgit:5.10.0.202012080955-r"
        const val jgitver = "fr.brouillard.oss:jgitver:0.12.0"
    }

    object OS {
        const val oshi = "com.github.oshi:oshi-core:5.2.5"
    }

    object Image {
        const val thumbnailator = "net.coobird:thumbnailator:0.4.14"
    }

    object Encoding {
        const val asn1 = "com.hierynomus:asn-one:0.1.0"
        const val asn1bean = "com.beanit:asn1bean:1.12.0"
        const val gifEncoder = "com.squareup:gifencoder:0.10.1"
        const val gifDecoder = "app.redwarp.gif:decoder:0.3.0"
        const val simplemagic = "com.j256.simplemagic:simplemagic:1.17"
    }

    object Compression {
        const val jvmbrotli = "com.nixxcode.jvmbrotli:jvmbrotli:0.2.0"
        const val brotli = "org.brotli:dec:0.1.2"
    }

    object ByteCode {
        const val asmVersion = "7.3.1"
        const val jandex = "org.jboss:jandex:2.2.1.Final"
        const val asm = "org.ow2.asm:asm:$asmVersion"
        const val asmUtil = "org.ow2.asm:asm-util:$asmVersion"
        const val byteBuddy = "net.bytebuddy:byte-buddy:1.9.7"
        const val classgraph = "io.github.classgraph:classgraph:4.8.102"
        const val hotswapAgent = "org.hotswapagent:hotswap-agent-core:1.4.1"
    }

    object JavaCpp {
        const val version = "1.7.0"
    }

    object Faker {
        const val java = "com.github.javafaker:javafaker:1.0.2"
        const val kotlin = "io.github.serpro69:kotlin-faker:1.5.0"
    }

    object Jwt {
        const val version = "0.10.5"
        const val api = "io.jsonwebtoken:jjwt-api:$version"
        const val impl = "io.jsonwebtoken:jjwt-impl:$version"
        const val jackson = "io.jsonwebtoken:jjwt-jackson:$version"
    }

    object OpenAPI {
        const val openapiGen = "org.openapitools:openapi-generator:4.3.1"
        const val swaggerUI = "org.webjars:swagger-ui:3.10.0"
        const val swaggerCli = "io.swagger.codegen.v3:swagger-codegen-cli:3.0.0"

        // OpenApi4j
        const val parser = "org.openapi4j:openapi-parser:1.0.5"
    }

    object ReactiveStreams {
        const val version = "1.0.3"
        const val core = "org.reactivestreams:reactive-streams:$version"
        const val flowAdapters = "org.reactivestreams:reactive-streams-flow-adapters:$version"
    }

    object Jfr {
        const val jfr2ctf = "de.marcphilipp.jfr2ctf:jfr2ctf:0.1.0"
        const val streaming = "com.microsoft.censum:jfr-streaming:1.0.0"
    }

    object Jmh {
        const val version = "1.21"
        const val jmhCore = "org.openjdk.jmh:jmh-core:$version"
        const val genAnnprocess = "org.openjdk.jmh:jmh-generator-annprocess:$version"
    }

    object K8S {
        const val javaClient = "io.kubernetes:client-java:11.0.0"
        const val fabric8 = "io.fabric8:kubernetes-client:5.0.0"
        const val operatorFramework = "io.javaoperatorsdk:operator-framework:1.7.1"
    }

    object Search {
        const val esKotlinClient = "com.github.jillesvangurp:es-kotlin-client:1.0.2"
    }

    object Apache {
        // Sql Parser
        const val calcite = "org.apache.calcite:calcite-core:1.26.0"
    }

    object Grid {
        // Hazelcast, Ignite, Coherence, Infinispan
        // https://search.maven.org/artifact/net.javacrumbs.shedlock/shedlock-parent/4.20.0/jar
    }

    object Web {

        object TeamVM {
            const val version = "0.6.1"
            const val classlib = "org.teavm:teavm-classlib:$version"
            const val jsoApis = "org.teavm:teavm-jso-apis:$version"
        }

        const val playwright = "com.microsoft.playwright:playwright:1.10.0"
    }

    const val jspecify = "org.jspecify:jspecify:0.2.0"
    const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"

    const val threetenExtra = "org.threeten:threeten-extra:1.7.0"
    const val micronautBom = "io.micronaut:micronaut-bom:2.0.0"
    const val mapstruct = "org.mapstruct:mapstruct:1.4.2.Final"
    const val ljv = "org.atp-fivt:ljv:1.02"

    const val streamex = "one.util:streamex:0.7.3"
    const val turbine = "app.cash.turbine:turbine:0.1.1"
    const val typetools = "net.jodah:typetools:0.6.2"
    const val funcj = "org.typemeta:funcj:0.6.16"

    const val nettyResolveDns = "io.netty:netty-resolver-dns:4.1.32.Final"
    const val jffi = "com.github.jnr:jffi:1.2.18"
    const val jnrUnixSocket = "com.github.jnr:jnr-unixsocket:0.21"
    const val commonsIO = "commons-io:commons-io:2.6"
    const val commonsCodec = "commons-codec:commons-codec:1.11"
    const val diffUtils = "io.github.java-diff-utils:java-diff-utils:4.9"

    const val jfreesvg = "org.jfree:jfreesvg:3.3"
    const val orsonpdf = "com.orsonpdf:orsonpdf:1.8"
    const val openpdf = "com.github.librepdf:openpdf:1.2.9"
    const val jimfs = "com.google.jimfs:jimfs:1.1"

    const val elasticmagic = "dev.evo.elasticmagic:elasticmagic:0.0.7"
    const val jgrapht = "org.jgrapht:jgrapht-core:1.5.0"
    const val jsonPathKt = "com.nfeld.jsonpathkt:jsonpathkt:2.0.0"
    const val jsonPath = "com.jayway.jsonpath:json-path:2.4.0"

    const val trov4j = "org.jetbrains.intellij.deps:trove4j:1.0.20181211"
    const val ff4j = "org.ff4j:ff4j-core:1.3.0"

    const val assertjCore = "org.assertj:assertj-core:3.11.1"
    const val googleTruth = "com.google.truth:truth:1.0.1"
    const val awaitility = "org.awaitility:awaitility:3.1.6"
    const val awaitilityKotlin = "org.awaitility:awaitility-kotlin:3.1.6"

    const val jol = "org.openjdk.jol:jol-core:0.9"
    const val japicmp = "com.github.siom79.japicmp:japicmp:0.13.0"
}

/** Dependency Extensions */
val DependencyHandler.KotlinBom get() = kotlin("bom")
val DependencyHandler.KotlinStdlibJdk8 get() = kotlin("stdlib-jdk8")
val DependencyHandler.KotlinReflect get() = kotlin("reflect")
val DependencyHandler.KotlinTest get() = kotlin("test")
val DependencyHandler.KotlinTestJunit get() = kotlin("test-junit")
val DependencyHandler.KotlinScriptRuntime get() = kotlin("script-runtime")
val DependencyHandler.KotlinScripUtil get() = kotlin("script-util")
val DependencyHandler.KotlinScripJsr223 get() = kotlin("scripting-jsr223")

/** PluginId Extensions */
inline val PluginDependenciesSpec.kotlinJvm get() = kotlin("jvm") version kotlinVersion
inline val PluginDependenciesSpec.kotlinxSerialization get() = kotlin("plugin.serialization") version kotlinVersion
inline val PluginDependenciesSpec.kotlinKapt get() = kotlin("kapt") version kotlinVersion

inline val PluginDependenciesSpec.kotlinSpring get() = kotlin("plugin.spring") version kotlinVersion
inline val PluginDependenciesSpec.kotlinAllOpen get() = kotlin("plugin.allopen") version kotlinVersion
inline val PluginDependenciesSpec.kotlinNoArg get() = kotlin("plugin.noarg") version kotlinVersion
inline val PluginDependenciesSpec.kotlinJpa get() = kotlin("plugin.jpa") version kotlinVersion

inline val PluginDependenciesSpec.kotlinScript get() = kotlin("plugin.scripting") version kotlinVersion
inline val PluginDependenciesSpec.dokka get() = id("org.jetbrains.dokka") version Deps.Dokka.version
inline val PluginDependenciesSpec.kotlinLibsPublisher get() = id("org.jetbrains.kotlin.libs.publisher") version Deps.Kotlin.libsPubVersion apply false
inline val PluginDependenciesSpec.kotlinDocsPublisher get() = id("org.jetbrains.kotlin.libs.doc") version Deps.Kotlin.libsPubVersion apply false

inline val PluginDependenciesSpec.exposed get() = id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
inline val PluginDependenciesSpec.kotlinxAtomicfu get() = id("kotlinx-atomicfu") version Deps.Kotlinx.atomicfuVersion
inline val PluginDependenciesSpec.binCompatValidator get() = id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.10.1"
inline val PluginDependenciesSpec.reflektPlugin get() = id("io.reflekt") version Deps.Kotlin.Reflekt.version

inline val PluginDependenciesSpec.kover get() = id("org.jetbrains.kotlinx.kover") version "0.5.1"
inline val PluginDependenciesSpec.sonarqube get() = id("org.sonarqube") version "3.4.0.2513" apply true

// Google Plugins
inline val PluginDependenciesSpec.ksp get() = id("com.google.devtools.ksp") version Deps.Kotlin.Ksp.version
inline val PluginDependenciesSpec.googleJib get() = id("com.google.cloud.tools.jib") version "3.2.1"

// Dependency Versions
inline val PluginDependenciesSpec.benmanesVersions get() = id("com.github.ben-manes.versions") version "0.42.0"
inline val PluginDependenciesSpec.versionCatalogUpdate get() = id("nl.littlerobots.version-catalog-update") version "0.5.1"
inline val PluginDependenciesSpec.consistentVersions get() = id("com.palantir.consistent-versions") version "1.28.0"

// Dependencies
inline val PluginDependenciesSpec.shadow get() = id("com.github.johnrengelman.shadow") version "7.1.2"
inline val PluginDependenciesSpec.dependencyAnalysis get() = id("com.autonomousapps.dependency-analysis") version "1.8.0"
inline val PluginDependenciesSpec.dependencyAnalyze get() = id("ca.cutterslade.analyze") version "1.9.0" apply true
inline val PluginDependenciesSpec.taskinfo get() = id("org.barfuin.gradle.taskinfo") version "1.4.0" apply false
inline val PluginDependenciesSpec.taskTree get() = id("com.dorongold.task-tree") version "1.5"
inline val PluginDependenciesSpec.forbiddenApis get() = id("de.thetaphi.forbiddenapis") version "3.1"
inline val PluginDependenciesSpec.extraJavaModuleInfo get() = id("de.jjohannes.extra-java-module-info") version "0.14"
inline val PluginDependenciesSpec.licensee get() = id("app.cash.licensee") version "1.4.1" apply false

inline val PluginDependenciesSpec.javafx get() = id("org.openjfx.javafxplugin") version "0.0.7"
inline val PluginDependenciesSpec.mrjar get() = id("me.champeau.mrjar") version "0.1"

// inline val PluginDependenciesSpec.mrjar get() = id("com.lingocoder.mrjar") version "0.0.16"
inline val PluginDependenciesSpec.protobuf get() = id("com.google.protobuf") version "0.8.18"
inline val PluginDependenciesSpec.changelog get() = id("org.jetbrains.changelog") version "0.4.0"

inline val PluginDependenciesSpec.kotless get() = id("io.kotless") version "0.1.6"
inline val PluginDependenciesSpec.kordampGradle get() = id("org.kordamp.gradle.project") version "0.40.0"
inline val PluginDependenciesSpec.jdeprscan get() = id("org.kordamp.gradle.jdeprscan") version "0.10.0"
inline val PluginDependenciesSpec.gradleEnforcer get() = id("org.kordamp.gradle.project-enforcer") version "0.7.0"
inline val PluginDependenciesSpec.jandex get() = id("org.kordamp.gradle.jandex") version "0.7.0"

// Benchmark
inline val PluginDependenciesSpec.jmh get() = id("me.champeau.gradle.jmh") version "0.6.4"
inline val PluginDependenciesSpec.jmhReport get() = id("io.morethan.jmhreport") version "0.9.0"

// Application frameworks
inline val PluginDependenciesSpec.micronautApplication get() = id("io.micronaut.application") version "1.0.1"
inline val PluginDependenciesSpec.micronautLibrary get() = id("io.micronaut.library") version "1.0.1"
inline val PluginDependenciesSpec.springboot get() = id("org.springframework.boot") version "2.2.6.RELEASE"
inline val PluginDependenciesSpec.springDepMgmt get() = id("io.spring.dependency-management") version "1.0.9.RELEASE"

// Static Analysis and Linting
inline val PluginDependenciesSpec.spotless get() = id("com.diffplug.spotless") version "6.7.2"
inline val PluginDependenciesSpec.spotlessChangelog get() = id("com.diffplug.spotless-changelog") version "2.4.0"
inline val PluginDependenciesSpec.spotbugs get() = id("com.github.spotbugs") version "4.3.0"
inline val PluginDependenciesSpec.ktlint get() = id("com.eden.orchidPlugin") version "9.3.0"
inline val PluginDependenciesSpec.detektPlugin get() = id("io.gitlab.arturbosch.detekt") version "1.15.0"
inline val PluginDependenciesSpec.detekt get() = id("io.github.detekt.gradle.compiler-plugin") version "0.3.3"
inline val PluginDependenciesSpec.qodanaPlugin get() = id("org.jetbrains.qodana") version "0.1.13"

// Artifact Publish
inline val PluginDependenciesSpec.nexusPublish get() = id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
inline val PluginDependenciesSpec.simpleMavenPublish get() = id("net.mbonnin.sjmp") version "0.2"
inline val PluginDependenciesSpec.jreleaser get() = id("org.jreleaser") version "0.2.0"
inline val PluginDependenciesSpec.mavenRepoAuth get() = id("org.hibernate.build.maven-repo-auth") version "3.0.4"
inline val PluginDependenciesSpec.gradleRelease get() = id("net.researchgate.release") version "2.8.1"
inline val PluginDependenciesSpec.githubRelease get() = id("com.github.breadmoirai.github-release") version "2.2.12"
inline val PluginDependenciesSpec.checksum get() = id("org.gradle.crypto.checksum") version "1.4.0"
inline val PluginDependenciesSpec.mavenPublish get() = id("com.vanniktech.maven.publish") version "0.13.0"

// Build config
inline val PluginDependenciesSpec.gitProperties get() = id("com.gorylenko.gradle-git-properties") version "2.4.1"
inline val PluginDependenciesSpec.buildconfig get() = id("com.github.gmazzo.buildconfig") version "3.0.2" apply false
inline val PluginDependenciesSpec.buildkonfig get() = id("com.codingfeline.buildkonfig") version "0.12.0" apply false

// Project version detection
inline val PluginDependenciesSpec.jgitPlugin get() = id("fr.brouillard.oss.gradle.jgitver") version "0.10.0-rc03"
inline val PluginDependenciesSpec.gitVersioner get() = id("io.toolebox.git-versioner") version "1.6.5"
inline val PluginDependenciesSpec.gitSemver get() = id("com.github.jmongard.git-semver-plugin") version "0.1.3"
inline val PluginDependenciesSpec.reckon get() = id("org.ajoberstar.reckon") version "0.12.0"
inline val PluginDependenciesSpec.gitChangelog get() = id("se.bjurr.gitchangelog.git-changelog-gradle-plugin") version "1.69.0"

// Static website
inline val PluginDependenciesSpec.mkdocs get() = id("ru.vyarus.mkdocs") version "2.1.1"
inline val PluginDependenciesSpec.orchid get() = id("com.eden.orchidPlugin") version "0.21.1"

// Packaging (JDK 14+) (https://openjdk.java.net/jeps/392)
inline val PluginDependenciesSpec.badassRuntime get() = id("org.beryx.runtime") version "1.11.4"
inline val PluginDependenciesSpec.badassjlink get() = id("org.beryx.jlink") version "2.22.3"
inline val PluginDependenciesSpec.badassJarPlugin get() = id("org.beryx.jar") version "1.2.0"
inline val PluginDependenciesSpec.javapackager get() = id("io.github.fvarrui.javapackager.plugin") version "1.5.1"
inline val PluginDependenciesSpec.jpackageplugin get() = id("org.panteleyev.jpackageplugin") version "0.0.2"

// Kotlin compiler plugins
inline val PluginDependenciesSpec.redacted get() = id("dev.zacsweers.redacted") version "1.1.0"
inline val PluginDependenciesSpec.kotlinPowerAssert get() = id("com.bnorm.power.kotlin-power-assert") version "0.11.0" apply false

// Parsers
inline val PluginDependenciesSpec.jflex get() = id("org.xbib.gradle.plugin.jflex") version "1.5.0"

// Template Engines
inline val PluginDependenciesSpec.jte get() = id("gg.jte.gradle") version Deps.TemplateEngine.Jte.version

// GraalVM
inline val PluginDependenciesSpec.nativeImage get() = id("org.graalvm.plugin.native-image") version "0.1.0-alpha2"

// JavaCpp
inline val PluginDependenciesSpec.javacppBuild get() = id("org.bytedeco.gradle-javacpp-build") version Deps.JavaCpp.version
inline val PluginDependenciesSpec.javacppPlatform get() = id("org.bytedeco.gradle-javacpp-platform") version Deps.JavaCpp.version

// Plugin development
inline val PluginDependenciesSpec.intellijPlugin get() = id("org.jetbrains.intellij") version "0.7.3"
inline val PluginDependenciesSpec.mavenPluginDev get() = id("de.benediktritter.maven-plugin-development") version "0.3.1"
