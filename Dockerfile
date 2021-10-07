# https://github.com/oracle/docker-images/blob/main/OracleJava/17/Dockerfile
# https://github.com/GoogleContainerTools/distroless/blob/main/examples/java/Dockerfile

# docker build --file Dockerfile --build-arg JDK_VERSION=18 --tag suresh/openjdk-playground --tag suresh/openjdk-playground:latest .
FROM openjdk:18-slim AS builder
# FROM debian:stable-slim
MAINTAINER Suresh

RUN set -eux; \
    apt update \
    && apt -y upgrade \
    && DEBIAN_FRONTEND=noninteractive apt install -y curl binutils;

ARG JDK_VERSION=18
ENV LANG en_US.UTF-8
ENV JAVA_PKG=https://download.oracle.com/java/${JDK_VERSION}/latest/jdk-${JDK_VERSION}_linux-x64_bin.tar.gz \
	  JAVA_HOME=/opt/java/openjdk

RUN set -eux; \
	  JAVA_SHA256=$(curl "$JAVA_PKG".sha256) ; \
	  curl --output /tmp/jdk.tgz "$JAVA_PKG" && \
	  echo "$JAVA_SHA256 */tmp/jdk.tgz" | sha256sum -c; \
	  mkdir -p "$JAVA_HOME"; \
	  tar --extract --file /tmp/jdk.tgz --directory "$JAVA_HOME" --strip-components 1

ENV PATH "${JAVA_HOME}/bin:${PATH}"

# JAVA_VERSION=$(sed -n '/^JAVA_VERSION="/{s///;s/"//;p;}' "${JAVA_HOME}/release");
# COPY ./docs/scripts/*.sh /scripts
# RUN ./scripts/openjdk-ea.sh ${JDK_VERSION}

COPY src/main/java/JavaApp.java /app
WORKDIR /app

RUN javac *.java \
    && jar cfe app.jar JavaApp *.class

RUN $JAVA_HOME/bin/jlink \
         --add-modules java.compiler,java.desktop,java.base \
         --verbose \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

CMD ["java", "--list-modules"]
# CMD ["jshell"]

#FROM gcr.io/distroless/java-debian10:base
#ARG JAR_FILE=openjdk-playground-1.2.0-12-main-uber.jar
#
#COPY --from=jreBuilder /jre /usr/lib/jre
#ENTRYPOINT ["/usr/lib/jre/bin/java", "-jar", "./app.jar"]
#COPY ./build/libs/${JAR_FILE} ./app.jar

