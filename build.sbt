name := "sbt-docker-launcher"

organization := "stebourbi"

version := "0.7-SNAPSHOT"

scalaVersion in Global := "2.10.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

sbtPlugin := true

publishMavenStyle := true

//http://local.repository:8081 or with prefix nexus http://repository/nexus
val deploymentRepository = sys.props.get("publish.repository")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishTo := {
  deploymentRepository.map(repo => {
    sbt.Keys.isSnapshot.value match {
      case true => Some("snapshots" at repo + "/content/repositories/snapshots")
      case _ => Some("releases" at repo + "/content/repositories/releases")
    }
  }).getOrElse(None)
}
