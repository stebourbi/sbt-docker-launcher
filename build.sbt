name := "sbt-docker-launcher"

organization := "stebourbi"

version := "1.0-SNAPSHOT"

scalaVersion in Global := "2.10.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

sbtPlugin := true

publishMavenStyle := true

libraryDependencies += "com.spotify" % "docker-client" % "3.6.3"

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

resolvers ++= {
  deploymentRepository.map(repo => {
   Seq(
    "own nexus snapshots" at s"${deploymentRepository}/content/repositories/snapshots",
    "own nexus releases" at s"${deploymentRepository}/content/repositories/releases",
    "maven central" at s"${deploymentRepository}/content/repositories/central/",
    "spray.io release" at s"${deploymentRepository}/content/repositories/spray.io.release/",
    "Typesafe Releases" at s"${deploymentRepository}/content/repositories/typesafe.release/",
    "Sonatype Releases" at s"${deploymentRepository}/content/repositories/sonatype.release/",
    "Cloudera Releases" at s"${deploymentRepository}/content/repositories/cloudera.release/"
  )}).getOrElse(Seq())
}
