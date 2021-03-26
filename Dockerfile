# FROM openjdk:17-slim-buster AS jreBuilder
# FROM openjdk:17-buster AS jreBuilder


FROM gcr.io/distroless/java-debian10:base AS jreLoomBuilder
ARG JDK_VERSION=loom
COPY ./docs/scripts/openjdk-ea.sh /scripts/openjdk-ea.sh
RUN ./scripts/openjdk-ea.sh ${JDK_VERSION}

ENV JAVA_HOME /usr/local/openjdk-17
ENV JAVA_VERSION $
ENV PATH $JAVA_HOME/bin:$PATH
ENV LANG C.UTF-8




RUN jlink \
        --add-modules java.compiler,java.desktop,java.naming,java.net.http,java.security.jgss,jdk.crypto.ec,java.sql,jdk.httpserver,jdk.jfr,jdk.management.jfr,jdk.management.agent,jdk.unsupported \
        --verbose \
        --strip-debug \
        --compress 2 \
        --no-header-files \
        --no-man-pages \
        --output /jre

FROM gcr.io/distroless/java-debian10:base
ARG JAR_FILE=openjdk-playground-1.0.0-79-uber.jar

COPY --from=jreBuilder /jre /usr/lib/jre
ENTRYPOINT ["/usr/lib/jre/bin/java", "-jar", "./app.jar"]
COPY ./build/libs/${JAR_FILE} ./app.jar


# https://github.com/GoogleContainerTools/distroless/blob/master/examples/java/Dockerfile
# https://github.com/sormuras/bach/blob/master/install-jdk.sh