package stebourbi.docker.launcher

import java.util

import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import com.spotify.docker.client.messages.{PortBinding, HostConfig, ContainerConfig}
import sbt.AutoPlugin
import sbt._


/**
 * User: slim
 * Date: 05/02/15
 * Time: 15:39
 *
 * TODO fill me please!
 *
 */
object DockerLauncherPlugin extends AutoPlugin {

  object autoImport {
    lazy val containers = SettingKey[Seq[ContainerInstanceDefinition]]("containers", "A list of docker containers to be launched")
    lazy val launchContainers = TaskKey[Unit]("launch-containers", "Launches defined docker containers.")
    lazy val stopContainers = TaskKey[Unit]("stop-containers", "Stops defined docker containers.")
    lazy val rmContainers = TaskKey[Unit]("rm-containers", "Remove defined docker containers.")
  }

  import autoImport._

  /**
   * Provide default settings
   */
  override lazy val projectSettings = Seq(

    containers := Seq(),

    launchContainers := {
      val logger = sbt.Keys.streams.value.log
      val docker = DefaultDockerClient.fromEnv().build()
      logger.info(s"stopping docker containers")
      try {
        containers.value.foreach(launch(docker, _, logger))
      } finally {
        docker.close()
      }
    },

    stopContainers := {
      val logger = sbt.Keys.streams.value.log
      val docker = DefaultDockerClient.fromEnv().build()
      logger.info(s"stopping docker containers")
      try {
        containers.value.foreach(stop(docker, _, logger))
      } finally {
        docker.close()
      }
    },

    rmContainers := {
      val logger = sbt.Keys.streams.value.log
      val docker = DefaultDockerClient.fromEnv().build()
      logger.info(s"stopping docker containers")
      try {
        containers.value.foreach(rm(docker, _, logger))
      } finally {
        docker.close()
      }
    }
  )


  def launch(dockerClient: DockerClient, definition: ContainerInstanceDefinition, logger: Logger) = {
    dockerClient.pull(definition.container.repository)
    val con: ContainerConfig = definition.apply(dockerClient)
    val id = dockerClient.createContainer(con, definition.container.name).id()
    dockerClient.restartContainer(id)
  }

  def stop(dockerClient: DockerClient, definition: ContainerInstanceDefinition, logger: Logger) = {
    dockerClient.stopContainer(definition.container.name, 10)
  }

  def rm(dockerClient: DockerClient, definition: ContainerInstanceDefinition, logger: Logger) = {
    dockerClient.removeContainer(definition.container.name)
  }

}

case class Container(val repository: String, val name: String)

case class ContainerInstanceDefinition(val container: Container
                                       , val tunneling: Seq[(Int, Int)] = Seq()
                                       , val environmentVariables: Seq[(String, String)] = Seq()
                                       , val links: Seq[(String, String)] = Seq()
                                        ) {

  def this(repository: String, name: String) = this(new Container(repository, name))

  def apply(dockerClient: DockerClient) = {
    import scala.collection.JavaConversions._
    val exposedPorts: Set[String] = tunneling.map(v => s"${v._2}").toSet
    val envVars: List[String] = environmentVariables.map(v => s"${v._1}=${v._2}").toList
    val linked: List[String] = links.map(v => s"${v._1}:${v._2}").toList

    val portBindings = new java.util.HashMap[String, java.util.List[PortBinding]]()
    for (port <- tunneling) {
      val hostPorts = new util.ArrayList[PortBinding]()
      hostPorts.add(PortBinding.of("0.0.0.0", port._1.toString))
      portBindings.put(port._2.toString, hostPorts)
    }


    val hostConfig = HostConfig.builder().links(linked).portBindings(portBindings).publishAllPorts(true).build()


    ContainerConfig.builder()
      .image(container.repository)
      .hostConfig(hostConfig)
      .exposedPorts(exposedPorts)
      .env(envVars)
      .build()
  }

  def p(tunnel: (Int, Int)): ContainerInstanceDefinition = this.copy(tunneling = tunnel +: tunneling)

  def e(environmentVariable: (String, String)): ContainerInstanceDefinition = this.copy(environmentVariables = environmentVariable +: environmentVariables)

  def link(link: (String, String)): ContainerInstanceDefinition = this.copy(links = link +: links)
}
