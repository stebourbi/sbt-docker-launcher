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
object DockerLauncherPlugin extends AutoPlugin{

  object SettingsAndTasks {
    lazy val containers = SettingKey[Seq[ContainerInstanceDefinition]]("containers", "A list of docker containers to be launched")
    lazy val launchContainers = TaskKey[Unit]("launch-containers", "Launches a docker definition.")
    lazy val stopContainers = TaskKey[Unit]("stop-containers", "Stops a definition")
  }

  import SettingsAndTasks._



  /**
   * Provide default settings
   */
  override lazy val projectSettings = Seq(

    containers := Seq(),

    launchContainers := {
      val logger = sbt.Keys.streams.value.log
      logger.info(s"launching docker containers")
      containers.value foreach(launch(_,logger))

    },

    stopContainers := {
      val logger = sbt.Keys.streams.value.log
      println(s"stopping docker containers")
      containers.value foreach(stop(_,logger))
    }
  )


  def launch(definition:ContainerInstanceDefinition,logger:Logger) = {
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

  def stop(definition:ContainerInstanceDefinition,logger:Logger) = {
    logger.info(s"stopping docker container $definition")
    val docker = OS.current match {
      case OS.Type.MacOs => OS.MacOs.get(logger)
    }
    val runningContainers = docker.ps()(logger)

    runningContainers.find {
      containerInstance => definition.container.name.equals(containerInstance.container.name)  //XXX
    }.map(docker.stop(_)(logger))

  }

}

case class Container(val repository:String,val name:String)
case class ContainerInstanceDefinition(val container:Container,val  tunneling:Seq[(Int,Int)]=Seq()){
  val command =  tunneling.map(p => s"-p ${p._1}:${p._2}").mkString(" ") + s" -P --name ${container.name} -d ${container.repository}"
}
case class ContainerInstance(val container:Container,val id:String,val status:ContainerStatus)
case object ContainerStatus extends Enumeration {
  type ContainerStatus = Value
  val Running,Paused,Stopped = Value
}

