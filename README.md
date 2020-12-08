# Module OpenJDK PlayGround

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/sureshg/openjdk-playground/CI?label=Build&logo=Github&style=for-the-badge)](https://github.com/sureshg/openjdk-playground/actions)
[![Docker Image Version](https://img.shields.io/docker/v/sureshg/openjdk-latest?color=RED&label=Docker%20Image&logo=Docker&logoColor=CYAN&style=for-the-badge)](https://hub.docker.com/r/sureshg/openjdk-latest)
[![OpenJDK Version](https://img.shields.io/badge/OpenJDK-Version--16-green?logo=java&style=for-the-badge&logoColor=cyan)](https://jdk.java.net/)
[![Kotlin release](https://img.shields.io/github/release/JetBrains/kotlin.svg?label=Kotlin&logo=kotlin&style=for-the-badge)](https://github.com/JetBrains/kotlin/releases/latest)
[![javadoc](https://javadoc.io/badge2/org.jetbrains.kotlin/kotlin-stdlib/javadoc.svg?logo=kotlin&style=for-the-badge)](https://javadoc.io/doc/org.jetbrains.kotlin/kotlin-stdlib)
<a href="https://deploy.cloud.run"><img src="https://deploy.cloud.run/button.svg" alt="CloudRun" title="CloudRun" width="18%"></a>

#### Setup OpenJDK (Loom Build)
```bash
$ curl -s "https://get.sdkman.io" | bash
$ sdk i java 16.ea.9.lm-open
$ sdk u java 16.ea.9.lm-open
```

#### Build
```bash
$ ./gradlew --console plain clean build

# Runs all checks
$ ./gradlew clean check

# Check tasks dependencies and version updates
$ ./gradlew clean build --dry-run
$ ./gradlew  dependencyUpdates

# List all available toolchains
$ ./gradlew -q javaToolchains
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
       -Djdk.tracePinnedThread=full \
       -jar build/libs/openjdk-latest-1.0.0-uber.jar

# Other Options
# -XX:+IgnoreUnrecognizedVMOptions
```

#### Run the application container
```bash
$ ./gradlew jibDockerBuild
$ docker run -it --rm --name openjdk-latest sureshg/openjdk-latest
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

 https://github.com/JakeWharton/picnic
 https://github.com/h0tk3y/better-parse
 https://github.com/actions/cache/blob/main/examples.md#java---gradle

 https://github.com/android/gradle-recipes

 CSS in Github README  - https://github.com/sindresorhus/css-in-readme-like-wat
 -->
