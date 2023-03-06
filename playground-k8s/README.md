### Run K8S Tests

Install [Rancher Desktop](https://rancherdesktop.io/) by selecting `dockerd(moby)` as the container engine.

```bash
$ ./gradlew :playground-k8s:test

# To watch the container stats, run
$ docker stats
```
