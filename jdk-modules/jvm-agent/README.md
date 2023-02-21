## Java Agent Module

##### Build & Run

```bash
$ ./gradlew :jdk-modules:jvm-agent:build
# List all the tasks of the module
$./gradlew :jdk-modules:jvm-agent:tasks --all
# Run the app with java agent.
$ java -jar jdk-modules/jvm-agent/build/libs/jvm-agent-*-all.jar
```
