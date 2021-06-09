### JVM Monitoring & Profiling

------

#### 1. Flight Recorder



#### 2. Java Mission Control



```bash
# https://adoptopenjdk.net/jmc.html
# http://jdk.java.net/jmc/
#

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



#### 3. Visualization

* [Jfr2Flame Converter](https://github.com/jvm-profiling-tools/async-profiler/releases/latest/download/converter.jar)

* [Jfr2Flame Converter Repo](https://github.com/jvm-profiling-tools/async-profiler/tree/master/src/converter)

* [D3 Flame Graph ](https://github.com/spiermar/d3-flame-graph)



#### 4. Profilers & Tools

* [Async Profiler](https://github.com/jvm-profiling-tools/async-profiler)

* [VisualVM](https://visualvm.github.io/)

* [FlameGraph](http://www.brendangregg.com/flamegraphs.html)



#### 5. Resources

* [JDK Mission Control Docs](https://docs.oracle.com/en/java/java-components/jdk-mission-control/)
* [Marcus Hirt's Blog](http://hirt.se/blog/?p=1312)
