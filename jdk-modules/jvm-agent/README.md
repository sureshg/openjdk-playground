## Java Agent Module

##### Build & Run

```bash
# Build the JFR jvm agent
$ ./gradlew :jdk-modules:jvm-agent:build

# List all the tasks of the module
$./gradlew :jdk-modules:jvm-agent:tasks --all

# Run the app with current java agent.
$ java --enable-preview -jar jdk-modules/jvm-agent/build/libs/jvm-agent-*-all.jar

# Run the app with Google allocation java agent from dependency.
$ ./gradlew :jdk-modules:jvm-agent:run --args="--allocation-agent"
```
