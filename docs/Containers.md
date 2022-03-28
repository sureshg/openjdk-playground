## Containers

------

### Offical Java Images

```bash
# Distroless Java Base (For Jlink apps based on debian bullseye)
# https://console.cloud.google.com/gcr/images/distroless/GLOBAL
$ docker pull gcr.io/distroless/java:base
$ docker pull gcr.io/distroless/java:base-nonroot
$ docker pull gcr.io/distroless/java-base:nonroot

# OS specific
$ docker pull gcr.io/distroless/java-debian11:base
$ docker pull gcr.io/distroless/java-debian11:base-nonroot

# AMD64/ARM64 Distroless Java Base
$ docker run --pull always -it --rm --platform=linux/arm64 gcr.io/distroless/java-base-debian11:nonroot
$ docker run --pull always -it --rm --platform=linux/amd64 gcr.io/distroless/java-base-debian11:nonroot

# Distroless OpenJDK
# https://github.com/GoogleContainerTools/distroless/tree/main/java
$ docker pull gcr.io/distroless/java:latest # OR
$ docker pull gcr.io/distroless/java-debian11:latest

# Distroless Static & Base
$ docker pull gcr.io/distroless/static:latest
$ docker pull gcr.io/distroless/base:latest

# Openjdk
# https://github.com/docker-library/openjdk
$ docker pull openjdk:19-slim
$ docker pull openjdk:19-alpine
$ docker pull openjdk:19-jdk-oracle

# Eclipse Temurin
# https://github.com/adoptium/containers#supported-images
$ docker pull eclipse-temurin:17-focal
$ docker pull eclipse-temurin:17-jre-focal
$ docker pull eclipse-temurin:17-alpine
$ docker pull eclipse-temurin:17-jre-alpine

# Oracle OpenJDK
$ docker pull container-registry.oracle.com/java/openjdk:latest

# Oracle Java (NFTC)
# https://github.com/oracle/docker-images/tree/main/OracleJava
$ docker pull container-registry.oracle.com/java/jdk:latest

# Microsoft OpenJDK
# https://docs.microsoft.com/en-us/java/openjdk/containers
$ docker pull mcr.microsoft.com/openjdk/jdk:17-ubuntu

# Azul Zulu
# https://github.com/zulu-openjdk/zulu-openjdk
$ docker pull azul/zulu-openjdk-debian:17-jre
$ docker pull azul/zulu-openjdk-alpine:17-jre

# Azul Zulu OpenJDK & Mission Control (Homebrew on MacOS)
$ brew tap mdogan/zulu
$ brew install <name>

# Azul Prime()
$ docker pull azul/prime:latest

# Liberica
# https://github.com/bell-sw/Liberica/tree/master/docker/repos/liberica-openjre-debian
$ docker pull bellsoft/liberica-openjdk-debian:latest
$ docker pull bellsoft/liberica-openjdk-alpine-musl:latest
$ docker pull bellsoft/liberica-openjdk-alpine:latest (libc)

# Amazon Corretto
# https://github.com/corretto/corretto-docker
$ docker pull amazoncorretto:17
$ docker pull amazoncorretto:17-alpine

# GraalVM CE & EE
# https://github.com/graalvm/container/pkgs/container/graalvm-ce
$ nerdctl pull ghcr.io/graalvm/graalvm-ce:latest
$ nerdctl pull ghcr.io/graalvm/native-image:java17-22
$ nerdctl pull container-registry.oracle.com/graalvm/enterprise:latest

# Redhat Univeral Base Images (UBI)
$ docker pull registry.access.redhat.com/ubi8/openjdk-11:1.10-1.1634738701

# GraalVM CE Dev Builds (No docker images available)
https://github.com/graalvm/graalvm-ce-dev-builds/releases/

# GraalVM builds from Gluon
https://github.com/gluonhq/graal/releases

# Jetbrains Runtime (No docker images available)
https://github.com/JetBrains/JetBrainsRuntime/releases

# JetBrains Projector (https://lp.jetbrains.com/projector/)
$


# Examples
$ docker run -it --rm gcr.io/distroless/java-debian10:base-nonroot openssl s_client --connect google.com:443

# Homebrew Zulu
https://github.com/mdogan/homebrew-zulu
```

- https://hub.docker.com/_/openjdk

- https://github.com/docker-library/docs/tree/master/openjdk

- https://github.com/AdoptOpenJDK/openjdk-docker#official-and-unofficial-images

- https://container-registry.oracle.com/java/openjdk

- https://www.graalvm.org/docs/getting-started/container-images/

##### Oracle JDK (NFTC)

* [Oracle Java SE Downloads](https://www.oracle.com/javadownload)

* [Oracle Java Archive](https://www.oracle.com/java/technologies/java-archive.html)

* [JDK Script Friendly URLs](https://www.oracle.com/java/technologies/jdk-script-friendly-urls/)

##### Docker Commands

```bash
# Remove all unused images, not just dangling ones
$ docker image prune -fa

# Docker build
$ docker build --file Dockerfile --build-arg JDK_VERSION=19 --tag suresh/openjdk-playground --tag suresh/openjdk-playground:latest .

# Docker Run
$ docker run -it --rm \
      --volume /var/run/docker.sock:/var/run/docker.sock \
      --volume "$HOME/app.yaml":"$HOME/app.yaml" \
      --volume  "$(pwd)":"$(pwd)":rw \
      --workdir "$(pwd)" \
      --entrypoint="run" \
      suresh/openjdk-playground:latest

# CMD (windows)
$ docker run -it --rm ^
    --volume /var/run/docker.sock:/var/run/docker.sock ^
    --volume "%cd%"/app:/app:rw ^
    --entrypoint="run" ^
    suresh/openjdk-playground:latest

# POWERSHELL
$ docker run -it --rm ,
    --volume /var/run/docker.sock:/var/run/docker.sock ,
    --volume ${pwd}/app:/app:rw ,
    --entrypoint="run" ,
    suresh/openjdk-playground:latest
```

##### Java Container logs

```bash
$ docker run \
        -it \
        --rm \
        --memory=256m \
        --cpus=2 \
        -v "$HOME":/app \
        -v /:/host \
        --name jdk-19 \
        openjdk:19-slim \
        java -XX:+UnlockExperimentalVMOptions \
        -XX:+UnlockDiagnosticVMOptions \
        -XX:+PrintFlagsFinal \
        -Xlog:os=trace,os+container=trace --version
        # | grep -e "Use.*GC" -e "Active"

```

##### Access Docker desktop LinuxKit VM on MacOS

```bash
$ docker run -it --rm --memory=256m --cpus=1 -v /:/host --name alpine alpine
 /# chroot /host
  # docker version
```

##### Netcat Webserver

```bash
FROM alpine

ENTRYPOINT while :; do nc -k -l -p $PORT -e sh -c 'echo -e "HTTP/1.1 200 OK\n\n hello, world"'; done
# https://github.com/jamesward/hello-netcat
# docker build -t hello-netcat .
# docker run -p 8080:8080 -e PORT=8080 -it hello-netcat
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

- ##### [Docker stats GraalVM app](https://github.com/vasilmkd/docker-stats-monitor/blob/master/Dockerfile)

**HTTP Proxy**

```bash
FROM ubuntu:20.04

ENV HTTP_PROXY="http://proxy.test.com:8080"
ENV HTTPS_PROXY="http://proxy.test.com:8080"
ENV NO_PROXY="*.test1.com,*.test2.com,127.0.0.1,localhost"

RUN apt-get update && apt-get upgrade -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y \
    opoenjdk \
    rm -rf /var/lib/apt/lists/*
```

#### Container Tools

------

* https://github.com/rancher-sandbox/rancher-desktop

* https://github.com/containerd/containerd

* https://github.com/lima-vm/lima (Linux on Mac)

* https://github.com/containerd/nerdctl

* https://github.com/k3s-io/k3s

* https://github.com/rancher/k3d/

* https://github.com/k0sproject/k0s

* https://kind.sigs.k8s.io/

* https://github.com/canonical/multipass (Running Ubuntu VM)

* https://podman.io/blogs/2021/09/06/podman-on-macs.html

* https://github.com/wagoodman/dive

* https://github.com/google/cadvisor

* https://github.com/google/go-containerregistry/tree/main/cmd/crane

* [Trivy - Container Scanning](https://github.com/aquasecurity/trivy)

* [CoSign - Container Signing](https://github.com/sigstore/cosign)

* [Kaniko - Build Images In Kubernetes](https://github.com/GoogleContainerTools/kaniko)

* [Colima - Container runtimes on MacOS ](https://github.com/abiosoft/colima)

#### Jlink

------

* https://www.morling.dev/blog/smaller-faster-starting-container-images-with-jlink-and-appcds/

* https://blog.adoptium.net/2021/08/using-jlink-in-dockerfiles/

* https://docs.microsoft.com/en-us/java/openjdk/java-jlink-runtimes

  * https://github.com/dsyer/sample-docker-microservice/

* https://mbien.dev/blog/entry/custom-java-runtimes-with-jlink

* https://jpetazzo.github.io/2020/03/01/quest-minimal-docker-images-part-2/

#### Distroless

------

https://github.com/GoogleContainerTools/distroless/blob/master/base/README.md

#### Documentation

------

https://spring-gcp.saturnism.me/deployment/docker/container-awareness

https://spring-gcp.saturnism.me/deployment/docker

https://www.infoq.com/presentations/openjdk-containers/

https://www.youtube.com/watch?v=Yj0sx1dvEdo

https://www.youtube.com/watch?v=2IpJABPzpvw

https://developers.redhat.com/devnation/tech-talks/containerjfr

http://blog.gilliard.lol/2019/12/04/Clojure-Containers.html

https://www.morling.dev/blog/smaller-faster-starting-container-images-with-jlink-and-appcds/

https://blog.arkey.fr/2020/06/28/using-jdk-flight-recorder-and-jdk-mission-control/

## Kubernetes

------

##### https://github.com/kubernetes-client/java

https://github.com/fabric8io/kubernetes-client

https://kubernetes.io/blog/2019/11/26/develop-a-kubernetes-controller-in-java/

https://github.com/ContainerSolutions/java-operator-sdk

https://operatorhub.io/

## Git

------

* https://github.com/newren/git-filter-repo
