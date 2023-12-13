# Containers & K8S

<!-- TOC -->
* [Containers & K8S](#containers--k8s)
    * [OpenJDK Container Images](#openjdk-container-images)
        * [Oracle JDK (NFTC)](#oracle-jdk-nftc)
    * [Docker Commands](#docker-commands)
    * [Debug Container](#debug-container)
    * [JVM Ergonomics and Container logs](#jvm-ergonomics-and-container-logs)
    * [JVM default GC](#jvm-default-gc)
    * [App Running on K8S/Docker](#app-running-on-k8sdocker)
    * [Access Docker desktop LinuxKit VM on MacOS](#access-docker-desktop-linuxkit-vm-on-macos)
    * [Multi Architecture Support](#multi-architecture-support)
    * [Netcat Webserver](#netcat-webserver)
    * [Container Runtime Interface](#container-runtime-interface)
    * [Container Tools {collapsible="true"}](#container-tools-collapsibletrue)
    * [Jlink {collapsible="true"}](#jlink-collapsibletrue)
    * [Documentation {collapsible="true"}](#documentation-collapsibletrue)
  * [Git {collapsible="true"}](#git-collapsibletrue)
<!-- TOC -->

### OpenJDK Container Images

```bash
# Distroless Java Images
# https://console.cloud.google.com/gcr/images/distroless/GLOBAL
# https://github.com/GoogleContainerTools/distroless#what-images-are-available
$ docker pull gcr.io/distroless/java-base-debian11:latest
$ docker pull gcr.io/distroless/java17-debian11:latest

# Distroless Static & Base
$ docker pull gcr.io/distroless/static-debian11:latest
$ docker pull gcr.io/distroless/base-debian11:latest

# GraalVM native-image base
$ docker pull cgr.dev/chainguard/graalvm-native-image-base:latest
$ docker pull cgr.dev/chainguard/static:latest
$ docker pull cgr.dev/chainguard/jdk:latest

# Openjdk
# https://github.com/docker-library/openjdk
$ docker pull openjdk:23-slim
$ docker pull openjdk:23-jdk-oracle
$ docker pull openjdk:19-alpine

# Eclipse Temurin
# https://github.com/adoptium/containers#supported-images
$ docker pull eclipse-temurin:20-jdk
$ docker pull eclipse-temurin:20-alpine

# Oracle OpenJDK
$ docker pull container-registry.oracle.com/java/openjdk:latest

# Oracle Java (NFTC)
# https://github.com/oracle/docker-images/tree/main/OracleJava
$ docker pull container-registry.oracle.com/java/jdk:latest

# Azul Zulu
# https://github.com/zulu-openjdk/zulu-openjdk
$ docker pull azul/zulu-openjdk-debian:20-jre
$ docker pull azul/zulu-openjdk-alpine:20-jre
$ docker pull azul/prime-debian:latest

# Amazon Corretto
# https://github.com/corretto/corretto-docker/tree/main/20/slim
$ docker pull amazoncorretto:20
$ docker pull amazoncorretto:20-alpine-jdk

# Microsoft OpenJDK
# https://learn.microsoft.com/en-us/java/openjdk/containers
$ docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu
$ docker pull mcr.microsoft.com/openjdk/jdk:17-distroless

# Liberica
# https://github.com/bell-sw/Liberica/tree/master/docker/repos/liberica-openjre-debian
$ docker pull bellsoft/liberica-openjdk-debian:latest
$ docker pull bellsoft/liberica-openjdk-alpine-musl:latest
$ docker pull bellsoft/liberica-openjdk-alpine:latest (libc)

# GraalVM CE & EE
# https://github.com/orgs/graalvm/packages
$ docker pull ghcr.io/graalvm/graalvm-community:latest
$ docker pull ghcr.io/graalvm/native-image-community:latest
$ docker pull ghcr.io/graalvm/native-image-community:muslib
$ docker pull container-registry.oracle.com/graalvm/native-image:latest

# GraalVM CE Dev Builds (No docker images available)
https://github.com/graalvm/graalvm-ce-dev-builds/releases/

# GraalVM builds from Gluon
https://github.com/gluonhq/graal/releases

# Jetbrains Runtime (No docker images available)
https://github.com/JetBrains/JetBrainsRuntime/releases

# Azul Zulu OpenJDK & Mission Control (Homebrew on MacOS)
# https://github.com/mdogan/homebrew-zulu
$ brew tap mdogan/zulu
$ brew install <name>

# Examples
$ docker run -it --rm gcr.io/distroless/java-debian11:base-nonroot openssl s_client --connect google.com:443
```

- https://hub.docker.com/_/openjdk
- https://github.com/docker-library/docs/tree/master/openjdk
- https://container-registry.oracle.com/java/openjdk
- https://www.graalvm.org/docs/getting-started/container-images/

##### Oracle JDK (NFTC)

* [Oracle Java SE Downloads](https://www.oracle.com/javadownload)
* [Oracle Java Archive](https://www.oracle.com/java/technologies/java-archive.html)
* [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/)

### Docker Commands

```bash
# Remove all unused images, not just dangling ones
$ docker system prune -af

# Docker Run
$ docker run -it --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v "$HOME/app.yaml":"$HOME/app.yaml" \
      -v  "$(pwd)":"$(pwd)":rw \
      --workdir "$(pwd)" \
      --entrypoint="run" \
      ghcr.io/sureshg/containers:openjdk-latest

# CMD (windows)
$ docker run -it --rm ^
    -v /var/run/docker.sock:/var/run/docker.sock ^
    -v "%cd%"/app:/app:rw ^
    --entrypoint="run" ^
    ghcr.io/sureshg/containers:openjdk-latest

# Find container IPAddress
$ docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' openjdk
```

### [Debug Container](https://github.com/iximiuz/cdebug)

```bash
# On M1 mac
$ docker run \
         --platform linux/amd64 \
         --pull always \
         -it \
         --rm \
         -p 8080:80 \
         --name openjdk-playground \
         ghcr.io/sureshg/containers:openjdk-latest

# Default busybox image should work.
$ brew install cdebug
$ cdebug exec \
         --privileged \
         -it \
         --rm \
         --platform linux/amd64 \
         docker://openjdk-playground

# Extract a file from the Docker environment and store it locally
$ docker run \
         --rm \
         --entrypoint cat \
         busybox:latest \
         '/bin/ls' > ls

# OR copy files from a container
$ docker cp <CONTAINER>:/app/app/jar .

# OR
$ id=$(docker create ghcr.io/sureshg/containers:openjdk-latest)
$ docker cp $id:/app/app.jar - > app.tar
$ docker rm -v $id
```

### JVM Ergonomics and Container logs

```bash
$ cat << EOF >App.java
import static java.lang.System.out;
public class App {
  void main() {
    var version = "• Java %s running on %s %s".formatted(
                   System.getProperty("java.version"),
                   System.getProperty("os.name"),
                   System.getProperty("os.arch"));
    out.println(version);
    long unit = 1024 * 1024L;
    long heapSize = Runtime.getRuntime().totalMemory();
    long heapFreeSize = Runtime.getRuntime().freeMemory();
    long heapUsedSize = heapSize-heapFreeSize;
    long heapMaxSize = Runtime.getRuntime().maxMemory();

    out.println("• [CPU] Active Processors: " + Runtime.getRuntime().availableProcessors());
    out.println("• [Mem] Current Heap Size (Committed) : " + heapSize/unit + " MiB");
    out.println("• [Mem] Current Free memeory in Heap  : " + heapFreeSize/unit + " MiB");
    out.println("• [Mem] Currently used memory         : " + heapUsedSize/unit + " MiB");
    out.println("• [Mem] Max Heap Size (-Xmx)          : " + heapMaxSize/unit + " MiB");
  }
}
EOF

$ docker run \
        -it \
        --rm \
        --cpus=2 \
        --memory=512m \
        --pull always \
        --mount type=bind,source=$(pwd),destination=/app,readonly \
        --mount type=bind,source=/,destination=/host,readonly \
        --name openjdk \
        openjdk:23-slim \
        java \
        --source 23 --enable-preview \
        -XX:+UnlockExperimentalVMOptions \
        -XX:+UnlockDiagnosticVMOptions \
        -XX:+PrintFlagsFinal \
        -XX:MaxRAMPercentage=0.8 \
        -XX:-MaxFDLimit \
        -Xlog:gc \
        /app/App.java \
        | grep -e "Use.*GC" -e "Active" -e "Using" -e "Max.*Limit" -e "Container" -e "•"
```

> For container logs, add **-Xlog:gc\*,os=trace,os+container=trace** option.
> Override the default container CPU quota detection **-XX:ActiveProcessorCount=<n>**

{title="GC Logs & ActiveProcessorCount" style="note"}

### JVM default GC

```bash
# https://github.com/openjdk/jdk/blob/master/src/hotspot/share/runtime/os.cpp#L1691
# OpenJDK reverts to Serial GC when it detects < 2 CPUs or < 2GB RAM
$ docker run -it --rm --cpus=1 --memory=1G openjdk:23-slim java -Xlog:gc --version
  #[0.007s][info][gc] Using Serial
```

* [**Stop using CPU limits on Kubernetes**](https://home.robusta.dev/blog/stop-using-cpu-limits)
* [JVM ergonomics](https://www.youtube.com/watch?v=wApqCjHWF8Q)
* https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/

### App Running on K8S/Docker

```bash
# Check if running on docker container
$ docker run \
     -it \
     --rm \
     --pull always \
     openjdk:23-slim \
     sh -c "cat /proc/self/cgroup | grep -i '/docker'"

# Check if running on Kubernets
$  docker run \
     -it \
     --rm \
     --pull always \
     openjdk:23-slim \
     sh -c "printenv | grep SERVICE"
```

* [Check if the container is running inside K8S](https://kubernetes.io/docs/concepts/services-networking/connect-applications-service/#environment-variables:~:text=printenv%20%7C%20grep%20SERVICE-,KUBERNETES_SERVICE_HOST,-%3D10.0.0.1%0AKUBERNETES_SERVICE_PORT%3D443)

### Access Docker desktop LinuxKit VM on MacOS

```bash
$ docker run -it --rm --memory=256m --cpus=1 --mount type=bind,source=/,destination=/host --name alpine alpine
 /# chroot /host
  # docker version
```

### [Multi Architecture Support](https://docs.docker.com/desktop/multi-arch/)

* [**tonistiigi/binfmt**](https://github.com/tonistiigi/binfmt)

  ```bash
  $ docker run --rm --privileged tonistiigi/binfmt --install all
  ```

* [**multiarch/qemu-user-static**](https://github.com/multiarch/qemu-user-static)

  ```bash
  $ docker run --rm --privileged multiarch/qemu-user-static --reset -p yes
  ```

* [Supported Targets - qemu-binfmt-conf.sh](https://github.com/qemu/qemu/blob/master/scripts/qemu-binfmt-conf.sh)
* [Dockerhub Supported Archs](https://github.com/docker-library/official-images#architectures-other-than-amd64)

* Examples

  ```bash
  $ docker run -it --rm --platform=linux/aarch64  alpine uname -m
    aarch64
  $ docker run -it --rm --platform=linux/amd64 alpine uname -m
    x86_64

  $ docker run --rm arm64v8/alpine uname -a
  $ docker run --rm arm32v7/alpine uname -a
  $ docker run --rm ppc64le/alpine uname -a
  $ docker run --rm s390x/alpine uname -a
  $ docker run --rm tonistiigi/debian:riscv uname -a
  ```

### Netcat Webserver

```bash
FROM alpine

ENTRYPOINT while :; do nc -k -l -p $PORT -e sh -c 'echo -e "HTTP/1.1 200 OK\n\n hello, world"'; done
# https://github.com/jamesward/hello-netcat
# docker build -t hello-netcat .
# docker run -p 8080:80 -e PORT=80 -it hello-netcat
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

[Shutdown signals and EnytryPoint](https://medium.com/@madflojo/shutdown-signals-with-docker-entry-point-scripts-5e560f4e2d45)

```bash
#!/bin/bash
## Entrypoint script for my-app. This script is to show how to write
## an entrypoint script that actually passes down signals from Docker.

## Load our DB Password into a runtime only Environment Variable
if [ -f /run/secrets/password ]
then
  echo "Loading DB password from secrets file"
  DB_PASS=$(cat /run/secrets/password)
  export DB_PASS
fi

## Run the Application
exec my-app
```

```dockerfile
ENTRYPOINT ["../../myapp-entrypoint.sh"]
```

- [Docker stats GraalVM app](https://github.com/vasilmkd/docker-stats-monitor/blob/master/Dockerfile)

**HTTP Proxy**

```bash
FROM debian:stable-slim

ENV HTTP_PROXY="http://proxy.test.com:8080"
ENV HTTPS_PROXY="http://proxy.test.com:8080"
ENV NO_PROXY="*.test1.com,*.test2.com,127.0.0.1,localhost"
```
### Container Runtime Interface

![Container Runtimes](containers.svg){ width="450px" border-effect="none" style="block" thumbnail="true" }


### Container Tools {collapsible="true"}

* https://github.com/rancher-sandbox/rancher-desktop
* https://github.com/beringresearch/macpine (Lightweight Linux VMs on MacOS)
* https://github.com/containerd/containerd
* https://github.com/lima-vm/lima (Linux on Mac)
* https://github.com/containerd/nerdctl
* https://github.com/k3s-io/k3s
* https://github.com/rancher/k3d/
* https://kind.sigs.k8s.io/
* https://github.com/k0sproject/k0s
* https://github.com/canonical/multipass (Running Ubuntu VM)
* https://podman.io/blogs/2021/09/06/podman-on-macs.html
* https://github.com/wagoodman/dive
* https://github.com/google/cadvisor
* https://github.com/google/go-containerregistry/tree/main/cmd/crane
* [Trivy - Container Scanning](https://github.com/aquasecurity/trivy)
* [CoSign - Container Signing](https://github.com/sigstore/cosign)
* [Kaniko - Build Images In Kubernetes](https://github.com/GoogleContainerTools/kaniko)
* [Colima - Container runtimes on MacOS ](https://github.com/abiosoft/colima)

### Jlink {collapsible="true"}

* https://www.morling.dev/blog/smaller-faster-starting-container-images-with-jlink-and-appcds/
* https://blog.adoptium.net/2021/08/using-jlink-in-dockerfiles/
* https://docs.microsoft.com/en-us/java/openjdk/java-jlink-runtimes
* https://github.com/dsyer/sample-docker-microservice/
* https://mbien.dev/blog/entry/custom-java-runtimes-with-jlink
* https://jpetazzo.github.io/2020/03/01/quest-minimal-docker-images-part-2/

### Documentation {collapsible="true"}

* https://spring-gcp.saturnism.me/deployment/docker/container-awareness
* https://spring-gcp.saturnism.me/deployment/docker
* https://www.infoq.com/presentations/openjdk-containers/
* https://www.youtube.com/watch?v=Yj0sx1dvEdo
* https://www.youtube.com/watch?v=2IpJABPzpvw
* https://developers.redhat.com/devnation/tech-talks/containerjfr
* http://blog.gilliard.lol/2019/12/04/Clojure-Containers.html
* https://www.morling.dev/blog/smaller-faster-starting-container-images-with-jlink-and-appcds/
* https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/

## Git {collapsible="true"}

* https://github.com/newren/git-filter-repo
