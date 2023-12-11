# Module OpenJDK PlayGround

<a href="https://foojay.io/today/works-with-openjdk">
 <img align="right" src="https://raw.githubusercontent.com/foojayio/badges/5007c3a6fecf22875b4ddf1aacb085569aec6dd8/works_with_openjdk/WorksWithOpenJDK.svg" width="7%" alt="WorksWithOpenJDK">
</a>

[![GitHub Workflow Status][gha_badge]][gha_url]
[![OpenJDK Version][java_img]][java_url]
[![Kotlin release][kt_img]][kt_url]
[![Docker Image][docker_img]][docker_url]
[![Style guide][ktfmt_img]][ktfmt_url]
[![SonarCloud][sonar_img]][sonar_url]
<a href="https://deploy.cloud.run"><img src="https://deploy.cloud.run/button.svg" alt="CloudRun" title="CloudRun" width="12%"></a>

#### Install OpenJDK

```bash
$ curl -s "https://get.sdkman.io" | bash
$ sdk i java openjdk-ea-23
```

#### Build

```bash
$ ./gradlew build

# Lint Github Actions
$ brew install actionlint
$ actionlint
```

#### Run the App

```bash
$ java -jar \
       --enable-preview \
       --add-modules=ALL-SYSTEM \
       -XX:+UseZGC -XX:+ZGenerational \
       build/libs/openjdk-playground-*-uber.jar

# Show all pinned virtual threads
$ jfr print --events jdk.VirtualThreadPinned openjdk-playground.jfr

# Show all socket events
$ jfr print --stack-depth 100 --events jdk.Socket\* openjdk-playground.jfr

# Virtual thread dump
$ jcmd dev.suresh.Main Thread.dump_to_file -format=json openjdk-playground-threads.json
```

#### Run using OpenJDK Image

```bash
$ docker run \
        -it \
        --rm \
        --pull always \
        --workdir /app \
        --publish 8000:8000 \
        --name openjdk-playground \
        --mount type=bind,source=$(pwd),destination=/app,readonly \
        openjdk:23-slim /bin/bash -c "./build/openjdk-playground && printenv && jwebserver -b 0.0.0.0 -p 8000 -d /"

# Download the JFR files
$ wget http://localhost:8000/tmp/openjdk-playground.jfr

# Build the container image
$ ./gradlew jibDockerBuild
$ docker run -it --rm --name openjdk-playground sureshg/openjdk-playground
```

#### SBOM and [Vulnerability Scan](https://github.com/google/osv-scanner/releases/latest)

```bash
$ ./gradlew cyclonedxBom

$ osv-scanner --sbom=build/sbom/bom.json
# OR use https://bomdoctor.sonatype.com/
```

#### Load testing the Web Server

- Check if the service is up and running!
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
- Run with Virtual threads.
    ```bash
    $ echo "GET https://127.0.0.1:8443/" | vegeta attack -insecure -duration=10s -name=VirtualThreads -rate=250 | tee vthread-results.bin | vegeta report
    $ vegeta report -type=json vthread-results.bin > vthread-metrics.json
    $ cat vthread-results.bin | vegeta report -type="hist[0,100ms,200ms,300ms]"
    $ cat vthread-results.bin | vegeta plot -title "Virtual Threads" > plot.html && open plot.html
    ```
- Combine the results and plot a single graph.
    ```bash
    $ vegeta plot -title "Threads vs Virtual Threads"  vthread-results.bin thread-results.bin > plot.html && open plot.html
    ```

#### Troubleshooting

* [Test Report](https://suresh.dev/openjdk-playground/reports/tests/test/)
* [Coverage Report](https://suresh.dev/openjdk-playground/reports/kover/html/)

<details>
  <summary>Commands</summary>

```bash
# Check the current version
$ ./gradlew -q --console plain version

# Publish to local repository
$ ./gradlew publishMavenPublicationToLocalRepository

# Stop Gradle and Kotlin daemon
$ ./gradlew --stop && pkill -f KotlinCompileDaemon

# Runs all checks
$ ./gradlew clean check

# Check tasks dependencies
$ ./gradlew clean build --dry-run

# Dep version updates
$ ./gradlew clean dependencyUpdates

# List all available toolchains
$ ./gradlew -q javaToolchains
```

</details>

##### Resources

* [Maven Deps Tree](https://github.com/SimonMarquis/Maven-Dependency-Tree)

 <!--
 Idiomatic Gradle  - https://github.com/jjohannes/idiomatic-gradle
                     https://github.com/jjohannes/gradle-demos/blob/main/java-17/ (Build Logic)

 https://docs.gradle.org/current/userguide/java_platform_plugin.html
 https://github.com/melix/jdoctor

 Http APIs to test - https://api.github.com/repos/jetbrains/kotlin
                   - https://httpbin.org/

 https://www.eclipse.org/jetty/documentation/current/high-load.html
 https://webtide.com/lies-damned-lies-and-benchmarks-2/


 CSS in Github README  - https://github.com/sindresorhus/css-in-readme-like-wat
 -->


[java_url]: https://jdk.java.net/

[java_img]: https://img.shields.io/badge/OpenJDK-23--ea-ea791d?logo=java&logoColor=ea791d

[kt_url]: https://github.com/JetBrains/kotlin/releases/latest

[kt_img]: https://img.shields.io/github/v/release/Jetbrains/kotlin?include_prereleases&color=7f53ff&label=Kotlin&logo=kotlin&logoColor=7f53ff

[mvn_search]: https://search.maven.org/search?q=g:io.micronaut

[mvn_jar]: https://search.maven.org/remote_content?g=io.micronaut&a=micronaut-http-server-netty&v=LATEST

[mvn_jar_img]: https://img.shields.io/maven-central/v/io.micronaut/micronaut-runtime?color=orange&label=micronaut&logo=apache-rocketmq&logoColor=orange

[gha_url]: https://github.com/sureshg/openjdk-playground/actions/workflows/build.yml

[gha_img]: https://github.com/sureshg/openjdk-playground/actions/workflows/build.yml/badge.svg

[gha_badge]: https://img.shields.io/github/actions/workflow/status/sureshg/openjdk-playground/build.yml?branch=main&color=green&label=Build&logo=Github-Actions&logoColor=green

[gh_pkgs]: https://github.com/sureshg/openjdk-playground/packages

[docker_img]: https://img.shields.io/docker/v/sureshg/openjdk-latest?color=dodgerblue&label=DockerHub&logo=docker&logoColor=dodgerblue

[docker_url]: https://hub.docker.com/r/sureshg/openjdk-playground

[sonar_img]: https://img.shields.io/badge/Sonar%20Cloud-Status-e46a2a.svg?logo=sonarcloud&logoColor=e46a2a

[sonar_url]: https://sonarcloud.io/summary/new_code?id=sureshg_openjdk-playground

[jmh_url]: https://openjdk.java.net/projects/code-tools/jmh/

[jmh_img]: https://img.shields.io/maven-central/v/org.openjdk.jmh/jmh-core?color=magenta&label=Jmh-Core&logo=apache%20maven&logoColor=magenta

[jmh-archetypes]: https://github.com/openjdk/jmh/tree/master/jmh-archetypes

[javadoc_url]: https://javadoc.io/doc/org.jetbrains.kotlin/kotlin-stdlib

[javadoc_img]: https://javadoc.io/badge2/org.jetbrains.kotlin/kotlin-stdlib/javadoc.svg?logo=kotlin

[sty_url]: https://kotlinlang.org/docs/coding-conventions.html

[sty_img]: https://img.shields.io/badge/style-Kotlin--Official-40c4ff.svg?style=for-the-badge&logo=kotlin&logoColor=40c4ff

[ktfmt_url]: https://github.com/facebookincubator/ktfmt#ktfmt

[ktfmt_img]: https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg?logo=kotlin&logoColor=FF4081
