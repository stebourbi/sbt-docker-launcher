
lazy val root = (project in file(".")).dependsOn(DockerLauncherPlugin)

lazy val DockerLauncherPlugin = uri("../..")

