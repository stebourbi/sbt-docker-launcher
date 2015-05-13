package stebourbi.docker.launcher

import sbt.AutoPlugin
import sbt._
import stebourbi.docker.launcher.ContainerStatus.ContainerStatus


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
      logger.info(s"launching docker containers")
      containers.value foreach (launch(_, logger))

    },

    stopContainers := {
      val logger = sbt.Keys.streams.value.log
      logger.info(s"stopping docker containers")
      containers.value foreach (stop(_, logger))
    },

    rmContainers := {
      val logger = sbt.Keys.streams.value.log
      logger.info(s"stopping docker containers")
      rm(containers.value, logger)
    }
  )


  def launch(definition: ContainerInstanceDefinition, logger: Logger) = {
    logger.info(s"launching docker container $definition")
    val docker = Docker.of(logger)

    val runningContainers = docker.psAll()(logger)
    logger.debug("docker ps: \n" + runningContainers.seq.mkString("\n"))

    runningContainers.find {
      instance => definition.container.name.equals(instance.container.name) //XXX
    } match {
      case Some(container) => {
        container.status match {
          case ContainerStatus.Stopped => docker.start(container)(logger)
          case ContainerStatus.Paused => docker.unpause(container)(logger)
          case ContainerStatus.Running => ()
        }
      }
      case None => docker.run(definition)(logger)
    }
  }

  def stop(definition: ContainerInstanceDefinition, logger: Logger) = {
    logger.info(s"stopping docker container $definition")
    val docker = Docker.of(logger)
    val runningContainers = docker.ps()(logger)

    runningContainers.find {
      containerInstance => definition.container.name.equals(containerInstance.container.name) //XXX
    }.map(docker.stop(_)(logger))

  }

  def rm(definitions: Seq[ContainerInstanceDefinition], logger: Logger) = {
    logger.info(s"rm docker container $definitions")
    val docker = Docker.of(logger)
    //TODO use ps when regex good
    val runningContainers = definitions.map(f => ContainerInstance(Container(f.container.name,f.container.name),f.container.name,ContainerStatus.from(f.container.name)))

    docker.rm(runningContainers)(logger)



  }

}

case class Container(val repository: String, val name: String)

case class ContainerInstanceDefinition(val container: Container
                                       , val tunneling: Seq[(Int, Int)] = Seq()
                                       , val environmentVariables: Seq[(String, String)] = Seq()
                                       , val links: Seq[(String, String)] = Seq()
                                        ) {

  def this(repository: String, name: String) = this(new Container(repository, name))

  val commandArguments = {
    val tunnels = tunneling.map(p => s"-p ${p._1}:${p._2}").mkString(" ")
    val envVars = environmentVariables.map(p => s"-e ${p._1}=${p._2}").mkString(" ")
    val linked = links.map(p => s"--link ${p._1}:${p._2}").mkString(" ")
    s" $tunnels $envVars $linked -P -d  --name ${container.name} ${container.repository}"
  }

  def p(tunnel: (Int, Int)): ContainerInstanceDefinition = this.copy(tunneling = tunnel +: tunneling)

  def e(environmentVariable: (String, String)): ContainerInstanceDefinition = this.copy(environmentVariables = environmentVariable +: environmentVariables)

  def link(link: (String, String)): ContainerInstanceDefinition = this.copy(links = link +: links)
}

case class ContainerInstance(val container: Container, val id: String, val status: ContainerStatus)

object ContainerInstance {
  val Unknown = ContainerInstance(Container("???", "???"), "???", ContainerStatus.Unknown)
}


case object ContainerStatus extends Enumeration {
  type ContainerStatus = Value
  val Running, Paused, Stopped, Unknown = Value

  def from(message: String): ContainerStatus = {
    message match {
      case m: String if m.toLowerCase.contains("paused") => ContainerStatus.Paused
      case m: String if m.startsWith("Up") => ContainerStatus.Running
      case m: String if m.startsWith("Exited") => ContainerStatus.Stopped
      case _ => ContainerStatus.Unknown
    }
  }

}

