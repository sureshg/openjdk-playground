### JVM Monitoring & Profiling

------

<!-- TOC -->

* [JVM Monitoring & Profiling](#jvm-monitoring--profiling)
    * [1. Flight Recorder](#1-flight-recorder)
    * [2. Java Mission Control](#2-java-mission-control)
    * [3. Visualization](#3-visualization)
    * [4. Profilers & Tools](#4-profilers--tools)
    * [5. Commands](#5-commands)
    * [6.JFR Streaming](#6jfr-streaming)
    * [7. HeapDump](#7-heapdump)
    * [8. ThreadDump](#8-threaddump)
    * [9. Unified GC Logging](#9-unified-gc-logging)
    * [10. System/Node Health](#10-systemnode-health)
    * [11. Commands](#11-commands)
    * [12. Resources](#12-resources)

<!-- TOC -->

#### 1. Flight Recorder

* [Java Flight Recorder Events](https://bestsolution-at.github.io/jfr-doc/index.html)
* [JFR Profile Configs](https://github.com/openjdk/jdk/tree/master/src/jdk.jfr/share/conf/jfr)

#### 2. Java Mission Control

```bash
# https://jdk.java.net/jmc/
# https://adoptium.net/jmc
# https://github.com/adoptium/jmc-build/releases
# https://github.com/corretto/corretto-jmc/releases
# https://www.oracle.com/java/technologies/jdk-mission-control.html
# https://www.azul.com/products/components/zulu-mission-control/

$ curl https://github.com/adoptium/jmc-build/releases/download/8.2.0/org.openjdk.jmc-8.2.0-macosx.cocoa.x86_64.tar.gz | tar xv -

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

* [OpenJDK Mission Control](https://github.com/openjdk/jmc)

* [Async Profiler](https://github.com/jvm-profiling-tools/async-profiler)

* [VisualVM](https://visualvm.github.io/)

* [FlameGraph](http://www.brendangregg.com/flamegraphs.html)

* [jconsole](https://github.com/openjdk/jdk/tree/master/src/jdk.jconsole)

* [jmxviewer](https://github.com/ivanyu/jmxviewer)

* [Cryostat](https://github.com/cryostatio/cryostat)

#### 5. Commands

- Wizard to create configuration files (.jfc)

  ```bash
  $ jfr configure --interactive
  $ jfr summary openjdk-playground.jfr
  $ jfr assemble <repository> <file> // cat jfr_file_version_* >> combined.jfr
  $ jfr print --events 'jdk.*' --stack-depth 64 openjdk-playground.jfr
  $ jfr print --events CPULoad,GarbageCollection openjdk-playground.jfr

  # Pinned virtual threads
  $  jfr print --events jdk.VirtualThreadPinned openjdk-playground.jfr
  ```

  Some config options are,

  ```bash
  -XX:StartFlightRecording:filename=recording.jfr
  -XX:FlightRecorderOptions:stackdepth=256

  -XX:+HeapDumpOnOutOfMemoryError
  -XX:ErrorFile=$USER_HOME/java_error_in_app_%p.log
  -XX:HeapDumpPath=$USER_HOME/java_error_in_app.hprof
  ```

* Find finalizable objects in your application

  ```bash
  $ java -XX:StartFlightRecording:filename=dump.jfr ...
  $ jfr print --events FinalizerStatistics dump.jfr
  ```

* [Troubleshoot Perf Issues Using JFR](https://docs.oracle.com/en/java/javase/18/troubleshoot/troubleshoot-performance-issues-using-jfr.html)

* [Flight Recorder Tool](https://docs.oracle.com/en/java/javase/18/troubleshoot/diagnostic-tools.html#GUID-D38849B6-61C7-4ED6-A395-EA4BC32A9FD6)

* [Flight Recorder API Guide](https://docs.oracle.com/en/java/javase/18/jfapi/flight-recorder-configurations.html)

#### 6.JFR Streaming

- [RemoteRecordingStream](https://egahlin.github.io/2021/05/17/remote-recording-stream.html)

- [Stream JFR files](https://github.com/microsoft/jfr-streaming)

#### 7. HeapDump

* [Solving Memory Leaks without Heap Dumps](http://hirt.se/blog/?p=1055)

* [Graal Heapdump Builder](https://www.graalvm.org/tools/javadoc/org/graalvm/tools/insight/heap/HeapDump.html)

* [Java Profiler Heap Dump Format](http://hg.openjdk.java.net/jdk6/jdk6/jdk/raw-file/tip/src/share/demo/jvmti/hprof/manual.html)

* [HPROF Parser](https://github.com/openjdk/jdk/blob/master/test/lib/jdk/test/lib/hprof/HprofParser.java)

* [Capture Java Heap Dumps](https://www.baeldung.com/java-heap-dump-capture)

#### 8. ThreadDump

```bash
# Get all java processes
$ jcmd -l
$ jps -mlvV

# See the available commands for all java processes
$ jcmd 0 help
# Use PID or main class
$ jcmd dev.suresh.Main VM.version
$ jcmd <dev.suresh.Main | pid> Thread.print

# Virtual thread dump
$ jcmd dev.suresh.Main Thread.dump_to_file -format=json openjdk-playground-threads.json

# OR
$ kill -3 <pid>
$ kill -3 "$(jcmd -l | grep "dev.suresh.Main" | cut -d " " -f1)"
# OR
# Prints additional info about thread (-e  extended listing) and locks (-l  long listing)
$ jstack -l -e  <pid>
```

#### 9. [Unified GC Logging](https://openjdk.java.net/jeps/158#Simple-Examples:)

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

#### 10. System/Node Health

* [SAR](https://github.com/sysstat/sysstat) (System Activity Report)

  ```bash
  $ yum install sysstat

  $ sar -A

  # Generate file for single day:
  $ sar -A -f /var/log/sa/sa19 > /tmp/sa19_$(hostname).txt

  # Generate file for multiple days files:
  $ ls /var/log/sa/sa?? | xargs -i sar -A -f {} > /tmp/sar_$(uname -n).txt

  ```

    - https://github.com/vlsi/ksar
    - https://github.com/sargraph/sargraph.github.io
    - https://sarcharts.tuxfamily.org/
    - https://www.cyberciti.biz/tips/top-linux-monitoring-tools.html


* [Node Exporter](https://prometheus.io/docs/guides/node-exporter/)

    - https://github.com/prometheus/node_exporter#docker


* [Prometheus JFR Exporter](https://github.com/rh-jmc-team/prometheus-jfr-exporter)

#### 11. Commands

* Grabbing a file from a remote system with ssh

  ```bash
  # grabbing a file from a remote system with ssh but no scp binary

  $ ssh user@remote-system "cat <file.txt" > file.txt
  $ ssh user@remote-system  'tar -S cz somedirectory' | tar -S xvz

  # Using Rsync: Upload
  $ rsync -Pavze ssh path/to/src remote-system:path/to/dst
  # Download
  $ rsync -Pavze ssh remote-system:path/to/src path/to/dst
  ```


* Strace

  ```bash
  # Syscall trace
  $ strace -tttTf -p <pid>
  ```


* Netstat

  ```bash
  $ netstat -tunalp

  # Socket stats
  $ ss -s
  ```

#### 12. Resources

* [JDK Mission Control Docs](https://docs.oracle.com/en/java/java-components/jdk-mission-control/)
* [Marcus Hirt's Blog](http://hirt.se/blog/?p=1312)
