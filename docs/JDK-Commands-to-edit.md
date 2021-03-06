// Jlink Commands
$ jlink --compress=2 --strip-debug --no-header-files --no-man-pages --add-modules java.base,jdk.jfr --module-path jmods  --output custom-jre

// Common options.
-Xms128m
-Xmx2048m
-ea
-XX:+UseCompressedOops
-Dfile.encoding=UTF-8
-Djava.net.preferIPv4Stack=true
-Djava.security.egd=file:/dev/./urandom


// Helpful NPE
$ java -XX:+ShowCodeDetailsInExceptionMessages Main.java
// -g enables debugging info (like local var name)
$ javac -g Main.java
$ java -XX:+ShowCodeDetailsInExceptionMessages Main

// NUMA
$ java -XX:+UseNUMA

// Remote Debugging
$ java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

// Build Tools


# List maven deps
$ ./mvnw -B -DoutputFile=target/deps.txt -DskipTests clean dependency:list install

// JCmd commandds
$ jcmd -l  // List all java process (or JPS)
$ jcmd <pid> // Get all properties
$ jcmd <pid> Thread.print // Thread Dump
$ jcmd <pid> VM.system_properties
$ jcmd <pid> VM.command_line
$ jcmd <pid> VM.flags



// Java Mission control (AdoptOpenJDk)
// Requires java 1.8
$ cd ~/install/openjdk/JMC/JDK Mission Control.app/Contents/MacOS
$ ./jmc -vm $JAVA_HOME/bin
or
$ open JDK\ Mission\ Control.app --args -vm ~/.jenv/versions/1.8/bin

GraalVM Options (Native Image)
------------------------------
// Install native-image
$ gu install native-image ruby R python

// Compiler Graphs
$ java -Dgraal.Dump=:3 -Dgraal.PrintGraph=Network -jar app.jar

// Build PGO enabled binary
$ /graal-xxx/bin/java -Dgraal.PGOInstrument=myclass.iprof MyClass
$ native-image --pgo=myclass.iprof MyClass

//Build Native Image
$ native-image --no-server                       \
               -J-Xmx3g                          \
               -J-Dapp.props=value         \  Sys property for java program
               -Dfoo=bar                   \  Image build/run-time system property
               --verbose                   \
               --static                    \
               --no-fallback               \
               --no-server                 \
               --report-unsupported-elements-at-runtime \
               --allow-incomplete-classpath             \
               --enable-https                           \

               -H:Name=micronaut-test      \  Biinary name
               -H:-ParseRuntimeOptions     \  Avoid parsing -D options at runtime.
               -H:IncludeResources=logback.xml|application.yml|bootstrap.yml \
               -H:ConfigurationFileDirectories=/var/config  \
               -H:+PrintUniverse                          \ Print information about classes, methods, and fields
               -H:+AddAllCharsets                         \
               -H:+ReportExceptionStackTraces             \ Print stacktrace of underlying exception
               -H:Class=micronaut.test.Application

 # Other misc configs.
              -H:+TraceClassInitialization
              -H:IncludeResourceBundles=test.HelpFormatterMessages \
              --initialize-at-run-time=java.lang.Math\$RandomNumberGeneratorHolder \
              --initialize-at-build-time                          \
              or --initialize-at-build-time=com.xxx..             \
              --enable-url-protocols=http,https                   \
              or -H:EnableURLProtocols=http,https
              --enable-all-security-services                      \
              -H:Log=registerResource:                            \
              -H:+PrintAnalysisStatistics                         \
              // New type flow analysts
              -H:+RemoveSaturatedTypeFlows                         \


https://github.com/graalvm/graalvm-demos/tree/master/native-image-configure-examples

// Assisted configuration for Native-Image
## or config-output-dir=
$ java  -agentlib:native-image-agent=experimental-class-loader-support,
                  config-merge-dir=${buildDir}/src/main/resources/META-INF/native-image/dev.suresh/my-app,
                  config-write-period-secs=300,
                  config-write-initial-delay-secs=5 ...

// List all image build options
$ native-image --expert-options-all

// Substrate VM dependency
```
 compileOnly "org.graalvm.nativeimage:svm"
```

// Docker build
```bash
FROM oracle/graalvm-ce:19.3.2-java11 as graalvm
COPY . /home/app/micronaut-test
WORKDIR /home/app/micronaut-test
RUN gu install native-image
RUN native-image --no-server --static -cp build/libs/micronaut-test-*-all.jar

FROM scratch
EXPOSE 8080
COPY --from=graalvm /home/app/micronaut-test/micronaut-test /app/micronaut-test
WORKDIR /app/micronaut-test
ENTRYPOINT ["/app/micronaut-test", "-Djava.library.path=/app"]
```

Savings are mainly due to:

➡️ No meta-data for dynamically loaded classes
➡️ No profiling data
➡️ No JIT compiler data structures
➡️ No dynamic code cache
➡️ Removing unused fields
➡️ No reduction of application payload memory **

Docker Commmands
-----------------

$  docker run \
  #      --cap-add=SYS_ADMIN
         --rm \
         -it \
         --name native-image \
         -p 8443:8443 \
         -v "$PWD":/usr/src/app \
         -w /usr/src/app \
         oracle/graalvm-ce:19.3.0.2-java11 \
         /bin/bash


#### SpringBoot
$ curl https://start.spring.io/starter.zip -d bootVersion=2.2.4.RELEASE -d dependencies=web -o demo.zip
$ unzip demo.zip
$ ./mvnw package
$ java -jar target/demo-0.0.1-SNAPSHOT.jar
$ curl -X GET http://localhost:8080/


#### Vegeta
echo "GET http://localhost:8080/long" | vegeta attack -duration=2s -rate=50/1s | tee results.bin | vegeta report\ncat results.bin | vegeta plot > plot.html && open plot.html

echo "GET http://:8080/long" | vegeta attack -duration=5s -name=Virtual-Thread-1k -rate=1000  > results.vthread.bin
echo "GET http://:8080/long" | vegeta attack -duration=10s -name=Virtual-Thread-1k -rate=1000  > results.vthread.bin
vegeta plot -title threads results.nthread.bin results.vthread.bin > plot.html


 sourceSets {
    "main" {
        projectDefault()
        java.srcDirs("../builtins-serializer/src",
                     "../javac-wrapper/src")
    }
    "test" { }
}



- Cloud ctl gradle kts update to github
- Reprodicuble gradle build
- Github action complete for dart and Graal AOT





---.github/no-response.yml -----
daysUntilClose: 14
responseRequiredLabel: "status/waiting on feedback"
closeComment: >
  This issue has been automatically closed because there has been no response
  after requesting feedback. Please feel free to re-open this issue if the
  scenario still exists and provide a comment with more information.
---------

mvn archetype:generate -DgroupId=dev.suresh -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
