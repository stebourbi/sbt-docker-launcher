FROM dockerfile/java:openjdk-7-jdk
MAINTAINER Stephane Jeandeaux <stephane.jeandeaux@gmail.com>

ENV SBT_VERSION 0.13.7

#Install sbt
RUN wget -O /tmp/sbt.deb http://dl.bintray.com/sbt/debian/sbt-${SBT_VERSION}.deb

RUN dpkg -i /tmp/sbt.deb
RUN rm -f /tmp/sbt.deb

#Update package
RUN apt-get update

#Install Docker
RUN apt-get -y install docker.io

ADD . /home/sbt-docker-launcher

CMD ["bash"]