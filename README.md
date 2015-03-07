[![Build Status](https://travis-ci.org/stebourbi/sbt-docker-launcher.svg?branch=linuxUserInGroup)](https://travis-ci.org/stebourbi/sbt-docker-launcher)

[ ![Codeship Status for stebourbi/sbt-docker-launcher](https://codeship.com/projects/12594c20-a190-0132-2b8b-5e7ffcf05096/status?branch=develop)](https://codeship.com/projects/65642)


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
