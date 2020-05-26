### Build & Run

```bash
# Set JDK to latest (Use SDKMAN)
$ sdk i java jdk-15-loom ~/openjdk/jdk-15-loom.jdk/Contents/Home/
$ sdk u java jdk-15-loom

# Build jar
$ ./gradlew clean build

# Run with preview features enabled. 
$ java -showversion --enable-preview -jar build/libs/openjdk-latest-0.0.1-uber.jar

# Check the dependency version updates.
$ ./gradlew  dependencyUpdates
```

### Load testing the Loom Web Server

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

