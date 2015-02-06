
//dependencyOverrides += "org.scala-sbt" % "sbt" % "0.13.7"

// build root test project
//lazy val root = Project("plugins", file(".")) dependsOn(DockerLauncherPlugin)
 
// depends on the DockerLauncherPlugin project
//lazy val DockerLauncherPlugin = file("..").getAbsoluteFile.toURI


addSbtPlugin("stebourbi"%"sbt-docker-launcher"%"1.0")
