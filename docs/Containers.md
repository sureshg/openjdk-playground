

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
