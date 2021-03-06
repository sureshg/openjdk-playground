

#### Containers

------

- Offical Java Images

  ```bash
  # Oracle OpenJDK
  $ docker pull container-registry.oracle.com/java/openjdk:latest
  $ docker run -it --rm --name openjdk container-registry.oracle.com/java/openjdk
  
  # Openjdk on DockerHub
  $ docker pull openjdk:15-jdk-slim
  ```

     - https://hub.docker.com/_/openjdk
     - https://github.com/docker-library/docs/tree/master/openjdk
     - https://github.com/AdoptOpenJDK/openjdk-docker#official-and-unofficial-images
     - https://container-registry.oracle.com/java/openjdk
     - https://www.graalvm.org/docs/getting-started/container-images/

  

- ##### Java Container logs

  ```bash
  $ docker run -it --rm --memory=256m --cpus=1 -v /:/host --name jdk-15 openjdk:15-jdk-slim java -Xlog:os=trace,os+container=trace -version
  ```



- ##### Access Docker desktop LinuxKit VM on MacOS

  ```bash
  $ docker run -it --rm --memory=256m --cpus=1 -v /:/host --name alpine alpine
   /# chroot /host
    # docker version
  ```



- ##### Netscat Webserver

  ```bash
  FROM alpine

  ENTRYPOINT while :; do nc -k -l -p $PORT -e sh -c 'echo -e "HTTP/1.1 200 OK\n\n hello, world"'; done
  # https://github.com/jamesward/hello-netcat
  # docker build -t hello-netcat .
  # docker run -p 8080:8080 -e PORT=8080 -it hello-netcat
  ```



- Forwards Logs

  ```bash
  # forward request and error logs to docker log collector
  RUN ln -sf /dev/stdout /var/log/nginx/access.log \
   && ln -sf /dev/stderr /var/log/nginx/error.log

  # OR output directly to
  /proc/self/fd/1 (STDOUT)
  /proc/self/fd/2 (STDERR)

  # https://docs.docker.com/config/containers/logging/configure/
  ```



- [Shutdown signals and EnytryPoint](https://medium.com/@madflojo/shutdown-signals-with-docker-entry-point-scripts-5e560f4e2d45)

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

#####  https://github.com/kubernetes-client/java

https://github.com/fabric8io/kubernetes-client

https://kubernetes.io/blog/2019/11/26/develop-a-kubernetes-controller-in-java/

https://github.com/ContainerSolutions/java-operator-sdk

https://operatorhub.io/

## Git

------

* https://github.com/newren/git-filter-repo
