import stebourbi.docker.launcher._
import stebourbi.docker.launcher.DockerLauncherPlugin.autoImport._


lazy val root = Project("root",file(".")).enablePlugins(DockerLauncherPlugin)

organization := "stebourbi"

name := "test-project"

version := "0.99"

sbtPlugin := true

containers := Seq(("redis","myredis"))

