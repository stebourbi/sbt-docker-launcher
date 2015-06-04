name := "sbt-docker-launcher"

organization := "stebourbi"

version := "0.7-SNAPSHOT"

scalaVersion in Global := "2.10.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

sbtPlugin := true

publishMavenStyle := true

val deploymentRepository = sys.props.get("publish.repository")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishTo := {
        if (sbt.Keys.isSnapshot.value)
          Some("snapshots" at deploymentRepository + "nexus/content/repositories/snapshots")
        else
          Some("releases" at deploymentRepository + "nexus/content/repositories/releases")
      }
 
 
