FROM dockerfile/java:openjdk-7-jdk
MAINTAINER Stephane Jeandeaux <stephane.jeandeaux@gmail.com>

ENV SBT_VERSION 0.13.7

#Install sbt
RUN wget -O /tmp/sbt.deb http://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb

RUN dpkg -i /tmp/sbt.deb
RUN rm -f /tmp/sbt.deb



ADD . /home/sbt-docker-launcher

#Install Docker
RUN curl -s https://get.docker.io/ubuntu/ | sh

#add users

RUN useradd userindockergroup -p $(perl -e'print crypt("123456", "docker")')
RUN gpasswd -a userindockergroup docker

RUN useradd userinsudogroup -p $(perl -e'print crypt("123456", "docker")')
RUN gpasswd -a userinsudogroup sudo



CMD /etc/init.d/docker start && /bin/bash