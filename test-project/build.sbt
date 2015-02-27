import stebourbi.docker.launcher._
import stebourbi.docker.launcher.DockerLauncherPlugin.autoImport._


lazy val root = Project("root",file(".")).enablePlugins(DockerLauncherPlugin)

organization := "stebourbi"

name := "test-project"

version := "0.99"

sbtPlugin := true

containers += ( ("redis:latest","redis1") p (6379,6379)  e ("VAR1","VAL1") )

containers += ( ("redis:latest","redis2") link ("redis1","master") e ("VAR2","VA!L2") )
