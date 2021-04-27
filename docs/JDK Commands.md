# JDK Commands

This document contains list of [Java/JDK](http://jdk.java.net/) commands which i find useful for my
day to day work.

[TOC]

### Java Commands

------

##### 1. Create Source Code Structure

```bash
$ mkdir -p src/{main,test}/{java,resources}
```

##### 2. Preview features

```bash
$ javac --enable-preview -release 15 Foo.java
$ java  --enable-preview Foo
```

- [JEP12](https://openjdk.java.net/jeps/12)

- [Preview Features](https://docs.oracle.com/en/java/javase/13/language/preview-language-and-vm-features.html)

- [Gradle - Enabling Java preview features](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:feature_preview)

##### 3. Java Platform Module Systems (JPMS)

```bash
$ java --list-modules
$ java --describe-module java.se
$ java --show-module-resolution java.base

# Replace upgradeable modules in the runtime image
$ java --upgrade-module-path $DIR
```

* [Java Compiler Upgradeable module]( https://docs.oracle.com/en/java/javase/15/docs/api/java.compiler/module-summary.html)

##### 4. Disassembles a class

```bash
$ javap -p -v <classfile>
```

* **[Javap Pastebin](https://javap.yawk.at/)**

##### 5. Parallel GC

 ```bash
$ java -XX:+UseParallelGC ...
 ```

##### 6. Show Java VM/Property Settings

```bash
$ java --version
$ java -Xinternalversion

$ java -XshowSettings:all --version

# Show all system properties
$ java -XshowSettings:properties --version

# VM extra options
$  java -XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal -version
```

* [**Java Options**](https://docs.oracle.com/en/java/javase/15/docs/specs/man/java.html)
* **[VM Options](https://www.oracle.com/java/technologies/javase/vmoptions-jsp.html)**

##### 7. Scan deprecated APIs

```bash
# Print dependency summary
$ jdeps -summary  app.jar

# Check the dependencies on JDK internal APIs
$ jdeps --jdk-internals --classpath libs.jar app.jar

# List of module dependences to be used in JLink
$ jdeps --ignore-missing-deps --print-module-deps  app.jar

# Generate module-info
$ jdeps --generate-module-info ./  app.jar

# List all deprecated APIs for a release
$ jdeprscan --for-removal --release 17 --list

# Scan deprecated APIs
$ jdeprscan --for-removal --release 17 app.jar
```

* [Java EE Maven artifacts](https://openjdk.java.net/jeps/320)

##### 8. JPMS

```bash
# Start ModuleMainClass
$ java -m jdk.httpserver -b 127.0.0.1

# Compile all modules at once (Start from the main module)
$ javac  --enable-preview \
         --release 16 \
         -parameters  \                    // Optional, Generate metadata for reflection on method parameters
         --add-modules ALL-MODULE-PATH \   // Optional, Root modules to resolve in addition to the initial modules
         --module-path ... \               // Optional, where to find application modules
         --module-source-path "src" \      // Source files for multiple modules
         -d classes  \
         --module MainApp                  // Compile only the specified module(s) 

# Package all modules (Create for all modules)
$ jar --create \
      --file mods/app.jar \
      --main-class dev.suresh.MainKt \
      --module-version 1.0 \    // Optional
      -C app/ classes resources // Or *.class
         
# Launch app
$ java  --enable-preview \
        --show-version \
        --show-module-resolution \
        --module-path mods \
        --module app   // OR <module>/<mainclass>        
```

* [JPMS Quickstart](https://openjdk.java.net/projects/jigsaw/quick-start)

* [Docs and Resources](https://openjdk.java.net/projects/jigsaw/)

* **[Java Modules Cheat Sheet](https://nipafx.dev/build-modules/)**

##### 9. [JVMCI (Graal) Compiler](https://openjdk.java.net/jeps/317)

```bash
$ java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler

# To see that Graal is actually being loaded
$ java -Dgraal.ShowConfiguration=info
```

- [GraalJS OpenJDK Demo](https://github.com/graalvm/graal-js-jdk11-maven-demo)

##### 10. JShell

```bash
# JShell with preview feature enabled
$ jshell --enable-preview

# Use custom startup script
$ jshell --enable-preview --startup DEFAULT --startup ~/calc.repl
```

##### 11. Loom config

```bash
# Loom Default ForkJoinPool scheduler config. The scheduler is based on 
# ForkJoinPool and it's setup to spin up additional underlying carrier 
# threads to help when there are virtual threads blocked in Object.wait. 
# The default maximum is 256.

$ java -Djdk.defaultScheduler.parallelism=1   // Default to available processors
       -Djdk.defaultScheduler.maxPoolSize=256 // Max(parallelism,256)

# Trace pinned thread while holding monitors.
$ java -Djdk.tracePinnedThreads=short|full

# Default scheduler algo
$ java -Djdk.defaultScheduler.lifo=false (FIFO by default)


# Loom Install
$ sdk rm java jdk-16-loom
$ sdk i java jdk-16-loom ~/install/openjdk/jdk-16-loom.jdk/Contents/Home
```

##### 12. [Unified GC Logging](https://openjdk.java.net/jeps/158#Simple-Examples:)

```bash
$  java -Xlog:help
```

```bash
# For G1GC
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:InitiatingHeapOccupancyPercent=70
-XX:+PrintGC
-XX:+PrintGCDateStamps
-XX:+PrintGCDetails
-Xloggc:/log/gclogs/app-gc.log
-XX:+UseGCLogFileRotation
-XX:NumberOfGCLogFiles=15
-XX:GCLogFileSize=10M
```

##### 13. JFR

```bash
-XX:StartFlightRecording:filename=recording.jfr
-XX:FlightRecorderOptions:stackdepth=256
```

```bash
-XX:+HeapDumpOnOutOfMemoryError
-XX:ErrorFile=$USER_HOME/java_error_in_app_%p.log
-XX:HeapDumpPath=$USER_HOME/java_error_in_app.hprof
```

* [Troubleshoot Perf Issues Using JFR](https://docs.oracle.com/en/java/javase/15/troubleshoot/troubleshoot-performance-issues-using-jfr.html#GUID-0FE29092-18B5-4BEB-8D8D-0CBA7A4FEA1D)
* [Flight Recorder Tool](https://docs.oracle.com/en/java/javase/15/troubleshoot/diagnostic-tools.html#GUID-D38849B6-61C7-4ED6-A395-EA4BC32A9FD6)
* [Flight Recorder API Guide](https://docs.oracle.com/en/java/javase/15/jfapi/flight-recorder-configurations.html)

##### 14. Kotlin + Graal Native-Image

```bash
$ kotlinc -version -verbose -include-runtime -Xuse-ir -java-parameters -jvm-target 11 -api-version 1.4 -language-version 1.4 -progressive App.kt -d app.jar

$ java -showversion -jar app.jar
$ native-image --no-fallback --no-server -jar app.jar

$ chmod +x app
$ file app
$ otool -L app
$ objdump -section-headers  app
$ time ./app
```

##### 15. Kotlin Script

```kotlin
#!/usr/bin/env -S kotlinc -script --
// sudo snap install --classic kotlin
// ./hello.main.kts

@file:DependsOn("com.github.ajalt:clikt:2.8.0")

println("Hello Kotlin Script")
```

##### 16. JMC

```bash
# https://adoptopenjdk.net/jmc.html
# http://jdk.java.net/jmc/
# http://hirt.se/blog/?p=1196

# JDK should be in - /Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home/
$ curl https://ci.adoptopenjdk.net/view/JMC/job/jmc-build/job/master/lastSuccessfulBuild/artifact/target/products/org.openjdk.jmc-8.0.0-SNAPSHOT-macosx.cocoa.x86_64.tar.gz | tar xv -

$ rm -rf "/Applications/JDK Mission Control.app"
$ mv "JDK Mission Control.app" /Applications
$ open '/Applications/JDK Mission Control.app'

# To run with a JDK
$ ./jmc -vm %JAVA_HOME%\bin
# or
$ open '/Applications/JDK Mission Control.app' --args -vm $JAVA_HOME/bin
```

- [VisualVM](https://visualvm.github.io/)

##### 17. Generics

- [GenericsFAQ](http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html)
- [How we got Generics we have](https://cr.openjdk.java.net/~briangoetz/valhalla/erasure.html)

##### 18. HeapDump

* [Graal Heapdump Builder](https://www.graalvm.org/tools/javadoc/org/graalvm/tools/insight/heap/HeapDump.html)

* [Java Profiler Heap Dump Format](http://hg.openjdk.java.net/jdk6/jdk6/jdk/raw-file/tip/src/share/demo/jvmti/hprof/manual.html)

* [HPROF Parser](https://github.com/openjdk/jdk/blob/master/test/lib/jdk/test/lib/hprof/HprofParser.java)



### IDEs and Tools

------

##### 1. IntelliJ Selection Modes

* All Actions - <kbd>CMD</kbd> + <kbd>SHIFT</kbd> + <kbd>A</kbd>
* Select all occurrences - <kbd>CMD</kbd> + <kbd>CTRL</kbd> + <kbd>G</kbd>
* Column selection mode - <kbd>CMD</kbd> + <kbd>SHIFT</kbd> + <kbd>8</kbd>
* Multiple Cursors - <kbd>SHIFT</kbd> + <kbd>ALT</kbd> + <kbd>Left Click</kbd>
* Multi Selection - <kbd>SHIFT</kbd> + <kbd>ALT</kbd> + Select as usual
* Caret Cloning - <kbd>SHIFT</kbd> + <kbd>ALT</kbd> + Middle click on end line
* Move line - <kbd>SHIFT</kbd> + <kbd>ALT</kbd> + <kbd>Arrow UP/DOWN</kbd>



### Networking & Security

------

##### 🚨 [Security Developer’s Guide](https://docs.oracle.com/en/java/javase/16/security/index.html)



##### 1. [Allow Unsafe Server Cert Change](https://github.com/openjdk/jdk/blob/6a905b6546e288e86322ae978a1f594266aa368a/src/java.base/share/classes/sun/security/ssl/ClientHandshakeContext.java#L35-L76)

   ```bash
# This is Unsafe. Don't use in prod.
-Djdk.tls.allowUnsafeServerCertChange=true
-Dsun.security.ssl.allowUnsafeRenegotiation=true
   ```

##### 2 . Debugging TLS

* https://docs.oracle.com/en/java/javase/16/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-4D421910-C36D-40A2-8BA2-7D42CCBED3C6

* https://docs.oracle.com/en/java/javase/16/security/java-secure-socket-extension-jsse-reference-guide.html

* https://github.com/sureshg/InstallCerts/blob/master/src/main/kotlin/io/github/sureshg/extn/Certs.kt

     ```bash
     # Turn on all debugging
     $ java -Djavax.net.debug=all
     
     # Override HostsFileNameService
     $ java -Djdk.net.hosts.file=/etc/host/style/file
     
     # Force IPv4
     $ java -Djava.net.preferIPv4Stack=true
     
     # The entropy gathering device can also be specified with the system property
     $ java -Djava.security.egd=file:/dev/./urandom
     ```

##### 2. Java Networking Properties

- http://htmlpreview.github.io/?https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/net/doc-files/net-properties.html

- https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html

| Config             | Description                                                  |
| :----------------- | :----------------------------------------------------------- |
| http.proxyHost     | The host name of the proxy server                            |
| http.proxyPort     | The port number, the default value being `80`                |
| http.nonProxyHosts | `|`. The patterns may start or end with a `*` for wildcards. Any host matching one of these patterns will be reached through a direct connection instead of through a proxy |

- **HTTP**

  ```bash
  $ java -Dhttp.proxyHost=webcache.example.com -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=”localhost|host.example.com” <URL>
  ```

- **Env vars**

  ```bash
  export HTTP_PROXY=http://USERNAME:PASSWORD@10.0.1.1:8080/
  export HTTPS_PROXY=https://USERNAME:PASSWORD@10.0.0.1:8080/
  export NO_PROXY=master.hostname.example.com
  ```

##### 3. [HTTP Client](https://openjdk.java.net/groups/net/httpclient/#incubatingAPI) Properties

```bash
-Djdk.internal.httpclient.disableHostnameVerification
-Djdk.httpclient.HttpClient.log=headers
-Djdk.internal.httpclient.debug=false
-Djdk.tls.client.protocols="TLSv1.2"
```

##### 4. GPG/OpenPGP

* [GPG Key Prepare](https://github.com/s4u/sign-maven-plugin/blob/master/src/site/markdown/key-prepare.md)
* [Renew GPG Key](https://gist.github.com/krisleech/760213ed287ea9da85521c7c9aac1df0)

​      `Note that the private key can never expire.`



### Gradle Kotlin DSL

------

##### 1. Docs

- ##### [Gradle Kotlin DSL Samples](https://github.com/gradle/kotlin-dsl-samples/tree/master/samples)

- ##### [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

- ##### [Migrating Guide to Kotlin DSL](https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/)

- ##### [Building Kotlin JVM Libraries](https://guides.gradle.org/building-kotlin-jvm-libraries/)

##### 2. [Name Abbrevation](https://docs.gradle.org/6.8/userguide/command_line_interface.html#sec:name_abbreviation)

```bash
$ ./gradlew --console=plain \
            --continuous \
            --dry-run \
            --no-daemon \
            -Dauthor=Suresh \
            -Pmyprop=myvalue \
            cle dU
```

##### 3. Create new Java `SourceSet`

  ```kotlin
sourceSets {
    create("java15") {
        java {
            srcDirs("src/main/java15")
        }
    }
}
  ```

- [SourceSet](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_source_sets)

##### 4. Custom SourceSet directories

```kotlin
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
    }
    test {
        java {
            setSrcDirs(listOf("test"))
        }
    }
}
```

##### 5. Kotlin SourceSets

```kotlin
kotlin {
    sourceSets {
        main {
            kotlin.srcDirs("...")
        }
        test {
            kotlin.srcDirs("...")
        }
        create("java11") {
            kotlin.srcDirs("src/main/java11")
        }

        val test by creating {}
    }
}

// or
sourceSets.main {
    withConvention(KotlinSourceSet::class) {
        kotlin.srcDirs("src/main/kotlin", "src-gen/main/kotlin")
    }
}

// Sources Jar
val sourcesJar by tasks.registering(Jar::class) {
    // kotlin.sourceSets.main.get().kotlin
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}
```

##### 6. Compile class path

```kotlin
sourceSets.main.get().compileClasspath
// kotlin.sourceSets.main.get().kotlin
```

##### 7. [Configure/Create Tasks](https://docs.gradle.org/current/userguide/kotlin_dsl.html#kotdsl:containers)

* ###### Eager

  ```kotlin
  val p: Jar = getByName<Jar>("jar")       // Get existing task
  val q: Jar = create<Jar>("jar") {}       // Create new task
  val r: Jar by getting(Jar::class)        // Get task using property delegate
  val s: Copy by creating(Copy::class) {}  // Create task using property delegate
  ```

* ###### Lazy ([Confiuration Avoidance API](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html))

  ```kotlin
  val t: TaskProvider<Jar> = named<Jar>("jar"){}        // Get existing task
  val u: TaskProvider<Jar> = register<Jar>("jar"){}     // Create new task
  val v: TaskProvider<Jar> by registering(Jar::class){} // Create task using property delegate
  val jar: TaskProvider<Task> by existing // Get task using property delegate
  
  val foo: FooTask by existing            // Take Task type from val (Kotlin 1.4)
  val bar: BarTask by registering {}      // Take Task type from val (Kotlin 1.4)
  ```

##### 8. [Enabling Java preview feature](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:feature_preview)

```kotlin
tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}
tasks.withType<Test> {
    jvmArgs("--enable-preview")
}
tasks.withType<JavaExec> {
    jvmArgs("--enable-preview")
}
```

##### 9. [Reproducible builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives)

```kotlin
withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
```

* **[reproducible-builds.org](https://reproducible-builds.org/docs/jvm/)**
* https://github.com/jvm-repo-rebuild/reproducible-central

##### 10. [Multi Release Jar](https://blog.gradle.org/mrjars)

```kotlin
// See https://github.com/melix/mrjar-gradle for more on multi release jars
val mrVersion = "11"
if (JavaVersion.current().isJava11Compatible) {
    sourceSets {
        create("java$mrVersion") {
            java {
                srcDirs("src/main/java$mrVersion")
            }
        }
    }

    tasks {
        // For each source set, Gradle will automatically create a compile task.
        val mrJavaCompile = named<JavaCompile>("compileJava${mrVersion}Java") {
            sourceCompatibility = mrVersion
            targetCompatibility = mrVersion
            options.compilerArgs = listOf("--release", mrVersion)
        }

        named<Jar>("jar") {
            into("META-INF/versions/$mrVersion") {
                from(sourceSets.named("java$mrVersion").get().output)
            }
            manifest.attributes(
                "Multi-Release" to "true"
            )

            dependsOn(mrJavaCompile)
        }
    }
}
```

##### 11. Dependencies

```bash

# Dependencies
$ ./gradlew -q dependencies --configuration implementation
$ ./gradlew -q dependencies --configuration runtimeClasspath

$ ./gradlew -q dependencyInsight --dependency kotlin --configuration runtimeClasspath


# Task Dependencies
$ ./gradlew clean build --dry-run

# Dependency updates
$ ./gradlew clean dependencyUpdates -Drevision=release

# Refresh dependencies
$ ./gradlew clean build --refresh-dependencies
# Or
$ rm -rf ~/.gradle/caches

```

* [Debugging Dependencies](https://docs.gradle.org/current/userguide/viewing_debugging_dependencies.html)

* **[Gradle Conflict Resolution](https://docs.gradle.org/current/userguide/dependency_resolution.html#sec:conflict-resolution)**

##### 12. Update Wrapper and others

```bash
$ ./gradlew wrapper --gradle-version=7.0 --distribution-type=bin

# Set system properties or tool options
$ JAVA_TOOL_OPTIONS=-Dhttps.protocols=TLSv1.2 ./gradlew build

# Displays the properties
$ ./gradlew properties

# Gradle run with arguments
$ ./gradlew run --args="<JFR_FILE>"
```



##### 13. Gradle Versions

   * https://services.gradle.org/versions

     

### Security & Certificates

------

##### 1. Truststore

To create a RootCA `PKCS#12` trust-store of the given URL

```bash
# Extract the server certificates.
$ echo -n | openssl s_client -showcerts -connect google.com:443 | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > globalsign.crt

# Create/Add trust store
$ keytool -importcert -trustcacerts -alias globalsign-rootca -storetype PKCS12 -keystore globalsign-rootca.p12 -storepass changeit -file globalsign.crt

# Add intermediate certs (Optional)
$ keytool -importcert -keystore globalsign-rootca.p12 -alias CA-intermediate -storepass changeit -file CA-intermediate.cer

# Show PKCS#12 info.
$ openssl pkcs12 -info -password pass:changeit -in globalsign-rootca.p12
$ keytool -list -keystore globalsign-rootca.p12 --storetype pkcs12 -storepass changeit

# Create a new PKCS#12 store from certs
$ openssl pkcs12 -export -chain -out keystore.p12 -inkey private.key -password pass:test123 \
                  -in client.crt -certfile client.crt -CAfile cacert.crt -name client-key \
                  -caname root-ca

```

##### 2. Add Certs to IntelliJ Truststore

```bash
$ cacerts="$HOME/Library/Application Support/JetBrains/GoLand2020.2/ssl/cacerts"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
$ keytool -importcert -trustcacerts -alias rootca -storetype PKCS12 -keystore $cacerts -storepass changeit -file "$HOME/Desktop/RootCA-SHA256.crt"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit

$ cacerts="$HOME/Library/Application Support/JetBrains/IntelliJIdea2020.2/ssl/cacerts"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
$ keytool -importcert -trustcacerts -alias rootca -storetype PKCS12 -keystore $cacerts -storepass changeit -file "$HOME/Desktop/RootCA-SHA256.crt"
$ keytool -list -keystore "$cacerts" -storetype pkcs12 -storepass changeit
```



### Maven Central Release

------

* https://github.com/sormuras/bach/blob/master/install-jdk.sh
* https://github.com/raphw/byte-buddy/blob/master/.github/workflows/main.yml#L92-L109
* https://github.com/raphw/byte-buddy/blob/master/.github/scripts/install-jdk.sh
* https://blog.autsoft.hu/publishing-an-android-library-to-mavencentral-in-2019/





### [Maven](https://search.maven.org/search?q=org.jetbrains.kotlin)

------

##### 1. [Public Maven Repositories](https://www.deps.co/guides/public-maven-repositories/)

- https://mvnrepository.com/repos
- https://maven.google.com/web/index.html

##### 2. [Create a Project](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

```bash
$ mvn archetype:generate -DgroupId=dev.suresh -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```

##### 3. [Enabling Java preview feature](https://blog.codefx.org/java/enable-preview-language-features/#Enabling-Previews-In-Tools)

```xml

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>3.6.2</version>
      <configuration>
        <release>15</release>
        <compilerArg>--enable-preview</compilerArg>
      </configuration>
    </plugin>

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <configuration>
        <argLine>--enable-preview</argLine>
      </configuration>
    </plugin>

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-failsafe-plugin</artifactId>
      <configuration>
        <argLine>--enable-preview</argLine>
      </configuration>
    </plugin>
  </plugins>
</build>
```

##### 4. [Reproducible Builds](https://maven.apache.org/guides/mini/guide-reproducible-builds.html)

```xml

<properties>
  <project.build.outputTimestamp>2020-04-02T08:04:00Z</project.build.outputTimestamp>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

##### 5. Maven Wrapper

```bash
$ mvn -N io.takari:maven:wrapper -Dmaven=3.8.1
```

##### 6. [Update Version number](http://www.mojohaus.org/versions-maven-plugin/)

```bash
$ ./mvnw versions:set -DnewVersion=1.0.1-SNAPSHOT -DprocessAllModules -DgenerateBackupPoms=false
$ ./mvnw versions:revert // Rollback
$ ./mvnw versions:commit
```

##### 7. [Dependency Tree](https://search.maven.org/search?q=fc:kotlin.text.Regex)

```bash
# Show all orginal versions, not the resolved ones.
$ ./mvnw dependency:tree
$ ./mvnw dependency:tree -Ddetail=true
$ ./mvnw dependency:tree -Dverbose -Dincludes=org.jetbrains.kotlin:kotlin-stdlib
$ ./mvnw clean verify

# Dependency and plugin updates
$ ./mvnw clean versions:display-dependency-updates versions:display-plugin-updates

# Search Artifacts by class
https://search.maven.org/search?q=fc:kotlin.text.Regex
```



### Microservices Starters

------

##### 1. [Micronaut](https://micronaut.io/launch/)

```bash
$ curl https://launch.micronaut.io/demo.zip -o demo.zip
$ unzip demo.zip -d demo && cd demo && ./gradlew run --continuous --watch-fs

// OR
$ curl 'https://launch.micronaut.io/create/DEFAULT/dev.suresh.sample-app?lang=kotlin&build=gradle&test=junit&javaVersion=JDK_11&features=http-client&features=data-jdbc&features=jdbc-hikari&features=kotlin-extension-functions&features=graalvm' \
     --compressed \
      -o sample-app.zip
```

##### 2. [SpringBoot](https://start.spring.io/)

```bash
$ curl https://start.spring.io/starter.zip \
    -d dependencies=devtools,web \
    -d type=gradle-project \
    -d language=kotlin \
    -d javaVersion=11 \
    -d packaging=jar \
    -d platformVersion=2.2.6.RELEASE \
    -d name=helloworld \
    -d groupId=dev.suresh \
    -d artifactId=helloworld \
    -d packageName=dev.suresh \
    -d description=Hello%20World%20App \
    -d baseDir=helloworld \
    -o helloworld.zip
```

- [SpringBoot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring and Kotlin Docs](https://docs.spring.io/spring/docs/current/spring-framework-reference/index.html)

##### 3. [Quarkus](https://code.quarkus.io/)

##### 4. [Gradle Initializr](https://gradle-initializr.cleverapps.io/)

```bash
$ curl 'https://gradle-initializr.cleverapps.io/starter?type=kotlin-application&testFramework=&dsl=kotlin&gradleVersion=6.3&archive=zip&projectName=helloworld-kotlin&packageName=dev.suresh&generate-project=' --compressed -o helloworld-kotlin.zip

// OR
$ gradle init --type <java-library|java-application|...> --dsl kotlin
```

##### 5. [Maven Archetype Quickstart](http://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

```bash
$ mvn archetype:generate -DgroupId=dev.suresh -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
```



### Misc

------

##### 1. [SDKMAN](https://sdkman.io/install)

##### 2. [jEnv](https://www.jenv.be/)

##### 3. [Codelabs](https://github.com/googlecodelabs/tools/tree/master/codelab-elements)

##### 4. [Typora](https://typora.io/)

##### 5. Github Actions

- [Github Actions For Java](https://help.github.com/en/actions/language-and-framework-guides/github-actions-for-java)

- [Github Maven Package](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem)

- [Workflow Syntax](https://help.github.com/en/actions/reference/workflow-syntax-for-github-actions)

- [Env Variables](https://help.github.com/en/actions/configuring-and-managing-workflows/using-environment-variables)

- [Expressions Syntax & Contexts](https://help.github.com/en/actions/reference/context-and-expression-syntax-for-github-actions)

- [Workflow Commands](https://help.github.com/en/actions/reference/workflow-commands-for-github-actions)

- [Share Data Between Jobs](https://help.github.com/en/actions/configuring-and-managing-workflows/persisting-workflow-data-using-artifacts)

- [Encrypted Secrets](https://help.github.com/en/actions/configuring-and-managing-workflows/creating-and-storing-encrypted-secrets)

- [Docker Action](https://github.com/actions/hello-world-docker-action)

- [GITHUB_TOKEN](https://help.github.com/en/actions/configuring-and-managing-workflows/authenticating-with-the-github_token)

- [Awesome Actions](https://github.com/sdras/awesome-actions)


* Docker hub push - https://github.com/marketplace/actions/build-and-push-docker-images

* [Github Registry](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-docker-for-use-with-github-packages)

  ```bash
  $ VERSION=$(date +%s)
  $ docker build . --file Dockerfile --tag docker.pkg.github.com/sureshg/repo/app:${VERSION}
  $ docker login docker.pkg.github.com --username sureshg --password ${{ secrets.GITHUB_TOKEN }}
  $ docker push docker.pkg.github.com/sureshg/repo/app:${VERSION}
  ```


* Service container

  ```yaml
  services:
     postgres:
       image: postgres:10.8
       env:
         POSTGRES_USER: postgres
         POSTGRES_PASSWORD: postgres
       options:
       ports:
         - 5432/tcp
   ...
   jobs.services.postgres.ports[5432]
  ```



### Mac OS

```bash
$  sudo xattr -cr /Applications/JDKMon.app
```



### LDAP Tools

* https://ldap.com/ldap-tools/



### Native-Image

---------

##### 1. [Workshop](https://github.com/krisfoster/Native-Image-Workshop)

##### 2. Graal Updater & Truffle

```bash
$ gu list
$ gu install native-image espresso

$ java -truffle [-options] class [args...]
$ java -truffle [-options] -jar jarfile [args...]
```

##### 3. SVM Substitutions

```java
package com.newrelic.jfr.subst;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "jdk.jfr.internal.JVM")
final class Target_jdk_jfr_internal_JVM {

  @Substitute
  public long getTypeId(Class<?> clazz) {
    return -1;
  }
}

// Another example
@TargetClass(className = "com.google.protobuf.UnsafeUtil")
final class Target_com_google_protobuf_UnsafeUtil {

  @Substitute
  static sun.misc.Unsafe getUnsafe() {
    return null;
  }
}
```

```xml

<dependency>
  <groupId>org.graalvm.nativeimage</groupId>
  <artifactId>svm</artifactId>
  <version>${graalvm.version}</version>
  <scope>provided</scope>
</dependency>
```

Find GraalVm used to generate the native-image

```bash
$ strings -a $(which native-image) | grep -i com.oracle.svm.core.VM
```

```bash
$ git commit --allow-empty -m "empty commit"
```

* Maven integration
  - https://www.graalvm.org/docs/reference-manual/native-image/#integration-with-maven
* Image configure at build and runtime
  - https://github.com/graalvm/graalvm-demos/tree/master/native-image-configure-examples
* GraalVm demos - https://github.com/graalvm/graalvm-demos
* https://medium.com/graalvm/simplifying-native-image-generation-with-maven-plugin-and-embeddable-configuration-d5b283b92f57
* https://github.com/oracle/graal/blob/master/substratevm/CLASS-INITIALIZATION.md
* https://search.maven.org/artifact/org.graalvm.nativeimage/svm/20.0.0/jar
* https://github.com/graalvm/graalvm-ce-dev-builds/releases/
* Examples:
    * https://github.com/38leinaD/bpmn-diff/tree/master/.github/workflows
    * https://github.com/micronaut-projects/micronaut-starter/tree/master/.github/workflows

* https://jamesward.com/2020/05/07/graalvm-native-image-tips-tricks/



### [Minecraft Server](https://github.com/itzg/docker-minecraft-server)

```bash
$ docker run -d -p 25565:25565 --name mc itzg/minecraft-server:adopt15
```



### Blogs

* https://www.marcobehler.com/guides/jdbc

* https://www.marcobehler.com/guides/java-databases

* https://vladmihalcea.com/jdbc-driver-maven-dependency/

* https://vladmihalcea.com/jdbc-driver-connection-url-strings/

* Tools

    * https://utteranc.es/ - Comments Widgets
    * https://orchid.run/wiki/learn/tutorials/how-to-document-kotlin
    * http://casual-effects.com/markdeep/
    * https://www.mkdocs.org/ (eg: https://github.com/hexagonkt/hexagon/tree/master/hexagon_site)
    * Github publish example
      - https://github.com/hexagonkt/hexagon/blob/master/.github/workflows/main.yml
      *

### Awesome Svgs

------

##### Illustrations

- https://undraw.co/illustrations
- https://www.manypixels.co/gallery/
- https://www.pixeltrue.com/free-illustrations

##### Background

- https://www.svgbackgrounds.com/

##### Icons

- https://icons.getbootstrap.com/
- https://material.io/resources/icons/?style=baseline (not sag)
- https://fontawesome.com/v4.7.0/icons/ (not sag)
- https://simpleicons.org/ (Brand Icons - good one)
- https://github.com/microsoft/fluentui-system-icons
- https://tablericons.com/
- https://tabler-icons.io/
- https://systemuicons.com/
- https://github.com/c5inco/intellij-icons-compose
- https://www.visiwig.com/icons/
- https://standart.io/
- https://thenounproject.com/
- https://www.flaticon.com/
- https://www.iconfinder.com/
- https://ionicons.com/
- https://icons8.com/

##### Emojis

- https://emojipedia.org/search/
- https://github.com/twitter/twemoji/tree/master/assets

##### Tools

- https://jakearchibald.github.io/svgomg/
- https://css-tricks.com/tools-for-optimizing-svg/
- https://imageoptim.com/mac
- https://xmlgraphics.apache.org/batik/
- https://github.com/svg/svgo-osx-folder-action

```bash
$ pip3 install mkdocs-macros-plugin
$ ./gradlew dokka
$ cp  docs/mkdocs.yml build/dokka
$ cp -R docs/images build/dokka/my-app
$ cp CHANGELOG.md build/dokka/my-app/CHANGELOG.md
$ cp README.md build/dokka/my-app/Overview.md
$ Fix the img reference on Overview.md
$ cd build/dokka
$ mkdocs build
```