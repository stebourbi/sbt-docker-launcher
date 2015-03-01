[![Build Status](https://travis-ci.org/stebourbi/sbt-docker-launcher.svg?branch=linuxUserInGroup)](https://travis-ci.org/stebourbi/sbt-docker-launcher)

# sbt-docker-launcher
sbt plugin that manages(start/stop) docker containers for integration tests


## Dockerfile (make makefile)

### Build

```sh
docker build -t sbt_docker_launcher .
```


### Run

```sh
docker run -t -i --privileged sbt_docker_launcher
```