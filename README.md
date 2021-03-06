# Module OpenJDK PlayGround

[![GitHub Workflow Status][gha_badge]][gha_url]
[![OpenJDK Version][java_img]][java_url]
[![Kotlin release][kt_img]][kt_url]
[![Docker Image][docker_img]][docker_url]
[![Style guide][ktlint_img]][ktlint_url]
<a href="https://deploy.cloud.run"><img src="https://deploy.cloud.run/button.svg" alt="CloudRun" title="CloudRun" width="18%"></a>

#### Setup OpenJDK (Loom Build)

```bash
$ curl -s "https://get.sdkman.io" | bash
$ sdk i java 17.ea.2.lm-open
$ sdk u java 17.ea.2.lm-open
```

#### Build

```bash
$ ./gradlew --console plain clean build
```

#### Run with `preview features` enabled

```bash
$ java --show-version \
       --enable-preview \
       -Xmx256M \
       -XX:+UseZGC \
       -XX:ConcGCThreads=2 \
       -XX:ZUncommitDelay=60 \
       -Xlog:gc\* \
       -XX:+PrintCommandLineFlags \
       -Djava.security.egd=file:/dev/./urandom \
       -Djdk.tracePinnedThreads=full \
       -jar build/libs/openjdk-playground-1.0.0-uber.jar

# Other Options
# -XX:+IgnoreUnrecognizedVMOptions
```

#### Run the application container

```bash
$ ./gradlew jibDockerBuild
$ docker run -it --rm --name openjdk-playground sureshg/openjdk-playground
```

#### Load testing the Loom Web Server

  -  Check if the service is up and running!
      ```bash
      $ curl -v -k https://localhost:8443
      ```

  - Set the os `ulimit` for Vegeta
      ```bash
      $ ulimit -n 4096 // FD limit
      $ ulimit -u 1024 // processes limit
      ```
  - Run with kernel threads.
      ```bash
      $ echo "GET https://127.0.0.1:8443/" | vegeta attack -insecure -duration=10s -name=Threads -rate=250 | tee thread-results.bin | vegeta report
      $ vegeta report -type=json thread-results.bin > thread-metrics.json
      $ cat thread-results.bin | vegeta report -type="hist[0,100ms,200ms,300ms]"
      $ cat thread-results.bin | vegeta plot -title "Normal Threads" > plot.html && open plot.html
      ```
  - Run wih Loom Virtual threads.
      ```bash
      $ echo "GET https://127.0.0.1:8443/" | vegeta attack -insecure -duration=10s -name=VirtualThreads -rate=250 | tee vthread-results.bin | vegeta report
      $ vegeta report -type=json vthread-results.bin > vthread-metrics.json
      $ cat vthread-results.bin | vegeta report -type="hist[0,100ms,200ms,300ms]"
      $ cat vthread-results.bin | vegeta plot -title "Virtual Threads" > plot.html && open plot.html
      ```
  - Combine the results and plot a single graph.
      ```bash
      $ vegeta plot -title "Threads vs Loom Virtual Threads"  vthread-results.bin thread-results.bin > plot.html && open plot.html
      ```

#### Troubleshooting

```bash
# Check the current version
$ ./gradlew -q --console plain version

# Stop Gradle and Kotlin daemon
$ ./gradlew --stop && pkill -f KotlinCompileDaemon

# Kotlin DSL Accessors
$ ./gradlew clean kotlinDslAccessorsReport

# Runs all checks
$ ./gradlew clean check

# Check tasks dependencies 
$ ./gradlew clean build --dry-run

# Dep version updates
$ ./gradlew  dependencyUpdates

# List all available toolchains
$ ./gradlew -q javaToolchains
```

##### JMPS Config
```java
// src/main/kotlin/module-info.java
module dev.suresh.openjdklatest {
  requires kotlin.stdlib.jdk8;
  requires java.net.http;
  requires org.eclipse.jetty.server;
  requires jdk.jfr;
  requires jdk.httpserver;
  requires shrinkwrap.resolver.api.maven;
  requires okhttp3;
  requires okhttp3.tls;
  requires okhttp3.mockwebserver;

  exports dev.suresh.loom.jetty;
  exports dev.suresh.mvn;
  exports dev.suresh.server;
}
```

 <!--
 // Template for IR based Kotlin compiler plugin
 https://github.com/bnorm/kotlin-ir-plugin-template

 Idiomatic Gradle  - https://github.com/jjohannes/idiomatic-gradle

 Http APIs to test - https://api.github.com/repos/jetbrains/kotlin
                   - https://httpbin.org/

 Cloud Run - https://github.com/jamesward/hello-kotlin-ktor
 Docker Builds - https://github.com/jamesward/comparing-docker-methods

 GC Tuning - https://docs.oracle.com/en/java/javase/15/gctuning/

 https://www.eclipse.org/jetty/documentation/current/high-load.html
 https://webtide.com/lies-damned-lies-and-benchmarks-2/

 https://github.com/marketplace/actions/download-openjdk
 https://github.com/sormuras/junit5-looming/blob/master/.github/workflows/main.yml

 https://github.com/actions/cache/blob/main/examples.md#java---gradle

 https://github.com/android/gradle-recipes

 CSS in Github README  - https://github.com/sindresorhus/css-in-readme-like-wat

 https://kordamp.org/kordamp-gradle-plugins/
 -->


[java_url]: https://jdk.java.net/
[java_img]: https://img.shields.io/badge/OpenJDK-Version--17-orange?logo=java&style=for-the-badge&logoColor=blue  

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest
[kt_img]: https://img.shields.io/github/release/JetBrains/kotlin.svg?label=Kotlin&logo=kotlin&style=for-the-badge

[mvn_search]: https://search.maven.org/search?q=g:io.micronaut
[mvn_jar]: https://search.maven.org/remote_content?g=io.micronaut&a=micronaut-http-server-netty&v=LATEST
[mvn_jar_img]: https://img.shields.io/maven-central/v/io.micronaut/micronaut-runtime?color=orange&label=micronaut&logo=apache-rocketmq&logoColor=orange&style=for-the-badge

[gha_url]: https://github.com/sureshg/openjdk-playground/actions
[gha_img]: https://github.com/sureshg/openjdk-playground/workflows/CI/badge.svg?branch=master
[gha_badge]: https://img.shields.io/github/workflow/status/sureshg/openjdk-playground/CI?color=green&label=Build&logo=Github-Actions&logoColor=green&style=for-the-badge
[gh_pkgs]: https://github.com/sureshg/openjdk-playground/packages

[docker_img]: https://img.shields.io/docker/v/sureshg/openjdk-playground?color=40c4ff&label=DockerHub&logo=docker&logoColor=40c4ff&style=for-the-badge
[docker_url]: https://hub.docker.com/r/sureshg/openjdk-playground

[sonar_img]: https://img.shields.io/sonar/quality_gate/io.sureshg:kotlin-scratchpad?logo=sonarcloud&server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge
[sonar_url]: https://sonarcloud.io/dashboard?id=io.sureshg%3Akotlin-scratchpad

[jmh_url]: https://openjdk.java.net/projects/code-tools/jmh/
[jmh_img]: https://img.shields.io/maven-central/v/org.openjdk.jmh/jmh-core?color=magenta&label=Jmh-Core&logo=apache%20maven&logoColor=magenta&style=for-the-badge
[jmh-archetypes]: https://github.com/openjdk/jmh/tree/master/jmh-archetypes

[javadoc_url]: https://javadoc.io/doc/org.jetbrains.kotlin/kotlin-stdlib
[javadoc_img]: https://javadoc.io/badge2/org.jetbrains.kotlin/kotlin-stdlib/javadoc.svg?logo=kotlin&style=for-the-badge

[sty_url]: https://kotlinlang.org/docs/reference/coding-conventions.html
[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff

[ktlint_url]: https://ktlint.github.io/
[ktlint_img]: https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?logo=kotlin&style=for-the-badge&logoColor=FF4081