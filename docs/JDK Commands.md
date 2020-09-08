# JDK Commands

This document contains list of [Java/JDK](http://jdk.java.net/) commands which i find useful for my day to day work.

[TOC]

####  Java Commands

------



#####  1. Create Source Code Structure

```bash
$ mkdir -p src/{main,test}/{java,resources}
```



##### 2. Preview features

```bash
$ javac --enable-preview -release 15 Foo.java
$ java  --enable-preview Foo

# JShell with preview feature enabled
$ jshell --enable-preview
```

- [JEP12](https://openjdk.java.net/jeps/12)

- [Preview Features](https://docs.oracle.com/en/java/javase/13/language/preview-language-and-vm-features.html)

- [Gradle - Enabling Java preview features](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:feature_preview)



##### 3. Java Platform Module Systems (JPMS)

```bash
$ java --list-modules
$ java --describe-module java.se
$ java --show-module-resolution java.base
```



##### 4. Disassembles a class

```bash
$ javap -p -v <classfile>
```

* **[Javap Pastebin](https://javap.yawk.at/)**



##### 5. Parallel GC

 ```bash
$ java -XX:+UseParallelGC ...
 ```



Scan deprecated APIs

```bash
$ jdeprscan --release 14 app.jar
```



**-XX:+IgnoreUnrecognizedVMOptions**

 `-XX:MaxRAM=16g  `

`-XX:MaxGCPauseMillis=30`



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

##### **Kotlin Script**

```bash
#!/usr/bin/env -S kotlinc -script --
// sudo snap install --classic kotlin
// ./hello.main.kts

@file:DependsOn("com.github.ajalt:clikt:2.8.0")

println("Hello Kotlin Script")
```





**JMC**

```bash
# https://adoptopenjdk.net/jmc.html
# http://jdk.java.net/jmc/
# http://hirt.se/blog/?p=1196

# JDK should be in - /Library/Java/JavaVirtualMachines/jdk-14.0.2.jdk/Contents/Home/
$ curl https://ci.adoptopenjdk.net/view/JMC/job/jmc-build/job/master/lastSuccessfulBuild/artifact/target/products/org.openjdk.jmc-8.0.0-SNAPSHOT-macosx.cocoa.x86_64.tar.gz | tar xv -

$ mv JDK\ Mission\ Control.app /Applications/
$ open '/Applications/JDK Mission Control.app'

# To run with a JDK
$ ./jmc -vm %JAVA_HOME%\bin
# or
$ open '/Applications/JDK Mission Control.app' --args -vm $JAVA_HOME/bin
```



#### Containers

------

##### Java Container logs

```bash
$ docker run -it --rm --memory=256m --cpus=1 -v /:/host --name jdk-15 openjdk:15-jdk-slim java -Xlog:os=trace,os+container=trace -version
```

##### Access Docker desktop LinuxKit VM on MacOS

```bash
$ docker run -it --rm --memory=256m --cpus=1 -v /:/host --name alpine alpine
 /# chroot /host
  # docker version
```

 Netscat Webserver

```bash
FROM alpine

ENTRYPOINT while :; do nc -k -l -p $PORT -e sh -c 'echo -e "HTTP/1.1 200 OK\n\n hello, world"'; done
# https://github.com/jamesward/hello-netcat
# docker build -t hello-netcat .
# docker run -p 8080:8080 -e PORT=8080 -it hello-netcat
```

Forwards Logs

```bash
# forward request and error logs to docker log collector
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
 && ln -sf /dev/stderr /var/log/nginx/error.log

# OR output directly to
/proc/self/fd/1 (STDOUT)
/proc/self/fd/2 (STDERR)

# https://docs.docker.com/config/containers/logging/configure/
```



####  Java Cryptography & Security

------



##### 🚨 [Security Developer’s Guide](https://docs.oracle.com/en/java/javase/14/security/index.html)



##### 1. [Allow Unsafe Server Cert Change](https://github.com/openjdk/jdk/blob/6a905b6546e288e86322ae978a1f594266aa368a/src/java.base/share/classes/sun/security/ssl/ClientHandshakeContext.java#L35-L76)

   ```bash
// This is Unsafe. Don't use in prod.
-Djdk.tls.allowUnsafeServerCertChange=true
-Dsun.security.ssl.allowUnsafeRenegotiation=true
   ```

https://github.com/sureshg/InstallCerts/blob/master/src/main/kotlin/io/github/sureshg/extn/Certs.kt

```bash
// Force IPv4
-Djava.net.preferIPv4Stack=true
// The entropy gathering device can also be specified with the system property
-Djava.security.egd=file:/dev/./urandom
```



##### 2. [Networking Proxy](https://docs.oracle.com/javase/8/docs/technotes/guides/net/proxies.html)

- **HTTP**

  ```bash
  http.proxyHost
  http.proxyPort
  http.nonProxyHosts
  ```



- Env vars

  ```bash
  export HTTP_PROXY=http://USERNAME:PASSWORD@10.0.1.1:8080/
  export HTTPS_PROXY=https://USERNAME:PASSWORD@10.0.0.1:8080/
  export NO_PROXY=master.hostname.example.com
  ```



-

| Protocol  |                            Config                            |                         Description                          |
| :-------: | :----------------------------------------------------------: | :----------------------------------------------------------: |
| **HTTP**  | <!-- The host name of the proxy server -->  <br/>**http.proxyHost** <br/><br/><!-- The port number, the default value being 80 --><br/>**http.proxyPort**<br/><br/><!-- A list of hosts that should be reached directly, bypassing the proxy. This is a list of patterns separated by '\|'. The patterns may start or end with a '*' for wildcards. Any host matching one of these patterns will be reached through a direct connection instead of through a proxy --><br/>**http.nonProxyHosts** | `java -Dhttp.proxyHost=webcache.example.com -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=”localhost|host.example.com” GetURL` |
| **HTTPS** |                                                              |                                                              |
| **SOCKS** |                                                              |                                                              |

```bash
/* The host name of the proxy server */
http.proxyHost
/* The port number, the default value being 80 */
http.proxyPort
/*  '|'. The patterns may start or end with a '*' for wildcards. Any host matching one of these patterns will be reached through a direct connection instead of through a proxy */
http.nonProxyHosts
```





#### Gradle Kotlin DSL

------

##### 1. Docs

- ##### [Gradle Kotlin DSL Samples](https://github.com/gradle/kotlin-dsl-samples/tree/master/samples)

- ##### [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

- ##### [Migrating Guide to Kotlin DSL](https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/)

- ##### [Building Kotlin JVM Libraries](https://guides.gradle.org/building-kotlin-jvm-libraries/)



##### 2. Create new Java `SourceSet`

  ```kotlin
sourceSets {
   create("java15") {
       java {
           srcDirs("src/main/java15")
       }
   }
}
  ```

-  [SourceSet](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:java_source_sets)



##### 3. Custom SourceSet directories

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



##### 4. Kotlin SourceSets

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

        val test by creating{}
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



##### 5. [Configure/Create Tasks](https://docs.gradle.org/current/userguide/kotlin_dsl.html#kotdsl:containers)

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



##### 6. [Enabling Java preview feature](https://docs.gradle.org/current/userguide/building_java_projects.html#sec:feature_preview)

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



##### 7. [Reproducible builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives)

```kotlin
withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
```



##### 8. [Multi Release Jar](https://blog.gradle.org/mrjars)

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

```bash
$ ./gradlew dependencies --configuration implementation  // Dependencies
$ ./gradlew clean build --dry-run   // Task Dependencies
```



#### Maven Central Release

------

* https://github.com/sormuras/bach/blob/master/install-jdk.sh
* https://github.com/raphw/byte-buddy/blob/master/.github/workflows/main.yml#L92-L109
* https://github.com/raphw/byte-buddy/blob/master/.github/scripts/install-jdk.sh
* https://blog.autsoft.hu/publishing-an-android-library-to-mavencentral-in-2019/
*



#### [Maven](https://search.maven.org/search?q=org.jetbrains.kotlin)

------

##### 1. [Public Maven Repositories](https://www.deps.co/guides/public-maven-repositories/)

-  https://mvnrepository.com/repos
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



##### 5. [Update Version number](http://www.mojohaus.org/versions-maven-plugin/)

```bash
$ ./mvnw versions:set -DnewVersion=1.0.1 -DprocessAllModules -DgenerateBackupPoms=false
$ ./mvnw versions:revert // Rollback
$ ./mvnw versions:commit
```

##### 6. [Dependency Tree](https://search.maven.org/search?q=fc:kotlin.text.Regex)

```bash
# Show all orginal versions, not the resolved ones.
$ ./mvnw dependency:tree -Dverbose -Dincludes=org.jetbrains.kotlin:kotlin-stdlib
$ ./mvnw clean verify

# Search Artifacts by class
https://search.maven.org/search?q=fc:kotlin.text.Regex
```



#### Microservices Starters

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



#### Misc

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

   ```bash
// Set env variable than can access from workflow env context.
echo "::set-env name=JAVA_HOME::$GRAALVM_HOME"
   ```

Release Action

Release-draft action



Release/*

* [Cache](https://github.com/actions/cache/blob/master/examples.md#java---gradle)

  ```yaml
  - uses: actions/cache@v1
    with:
      path: ~/.m2/repository
      key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
      restore-keys: |
        ${{ runner.os }}-maven-
  ```

  ```yaml
  - uses: actions/cache@v1
    with:
      path: ~/.gradle/caches
      key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
      restore-keys: |
        ${{ runner.os }}-gradle-
  ```

  * String format

  ```bash
   ${{ hashFiles(format('{0}{1}', github.workspace, '/test.lock')) }}
  ```

  * Use outout between steps

    ```yaml
    - name: Get cache directory
      id: app-cache
      run: |
        echo "::set-output name=dir::$(command --cache-dir)"
    - uses: actions/cache@v1
      with:
        path: ${{ steps.app-cache.outputs.dir }}
    ```

  * Check runner OS

    ```YAML
    if: startsWith(runner.os, 'Linux')
    if: startsWith(runner.os, 'macOS')
    if: startsWith(runner.os, 'Windows')

    // check if security bot
    $ if: github.base_ref == 'master' && github.actor == 'dependabot[bot]'
    ```



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
     ${{ jobs.services.postgres.ports[5432] }}
    ```



Dart

----

Dev_dependency

Build_version

Build_runner

Create extension repo.

pub run build_runner build

pub run build_runner watch

pedantic

Json

------

Dep: json_annotations

Dev: json_serializable

Value

------

Build_value

Build_collections

Build_cli_annotations

Build_cli

Source_gen

Yaml

-----

checked_yaml







https://github.com/GoogleContainerTools/distroless

https://github.com/GoogleContainerTools/jib



Native-Image

---------

```java
package com.newrelic.jfr.subst;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className="jdk.jfr.internal.JVM")
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



* Maven integration - https://www.graalvm.org/docs/reference-manual/native-image/#integration-with-maven
* Image configure at build and runtime - https://github.com/graalvm/graalvm-demos/tree/master/native-image-configure-examples
* GraalVm demos - https://github.com/graalvm/graalvm-demos
* https://medium.com/graalvm/simplifying-native-image-generation-with-maven-plugin-and-embeddable-configuration-d5b283b92f57
* https://github.com/oracle/graal/blob/master/substratevm/CLASS-INITIALIZATION.md
* https://search.maven.org/artifact/org.graalvm.nativeimage/svm/20.0.0/jar
* https://github.com/graalvm/graalvm-ce-dev-builds/releases/
* Examples:
  * https://github.com/38leinaD/bpmn-diff/tree/master/.github/workflows
  * https://github.com/micronaut-projects/micronaut-starter/tree/master/.github/workflows

* https://jamesward.com/2020/05/07/graalvm-native-image-tips-tricks/

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
  * Github publish example - https://github.com/hexagonkt/hexagon/blob/master/.github/workflows/main.yml
  *

#### Awesome Svgs

#####    Illustrations

- https://undraw.co/illustrations
- https://www.manypixels.co/gallery/
- https://www.pixeltrue.com/free-illustrations

#####    Background

-  https://www.svgbackgrounds.com/

#####     Icons

- https://icons.getbootstrap.com/
- https://material.io/resources/icons/?style=baseline (not sag)
- https://fontawesome.com/v4.7.0/icons/ (not sag)
- https://simpleicons.org/ (Brand Icons - good one)
- https://systemuicons.com/
- https://www.visiwig.com/icons/
- https://standart.io/
- https://thenounproject.com/
- https://www.flaticon.com/
- https://ionicons.com/

#####     Emojis

- https://emojipedia.org/search/
- https://github.com/twitter/twemoji/tree/master/assets

#####     Tools

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
