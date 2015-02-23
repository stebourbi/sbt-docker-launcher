import stebourbi.docker.launcher._
import stebourbi.docker.launcher.DockerLauncherPlugin.SettingsAndTasks._

lazy val root = (project in file(".")).enablePlugins(DockerLauncherPlugin)

organization := "stebourbi"

name := "test-project"

version := "0.99"

containers := Seq(ContainerInstanceDefinition(Container("centos:6.6","centos")))  //FIXME better DSL
