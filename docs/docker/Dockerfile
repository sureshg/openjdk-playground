# Using Google Distroless
# https://github.com/GoogleContainerTools/distroless/blob/master/examples/java/Dockerfile
FROM openjdk:11-jdk-slim AS build-env
ADD . /app/examples
WORKDIR /app
RUN javac examples/*.java
RUN jar cfe main.jar examples.HelloJava examples/*.class

FROM gcr.io/distroless/java:11
COPY --from=build-env /app /app
WORKDIR /app
CMD ["main.jar"]