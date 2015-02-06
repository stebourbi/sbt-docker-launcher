package stebourbi.docker.launcher

import sbt.AutoPlugin
import sbt._


/**
 * User: slim
 * Date: 05/02/15
 * Time: 15:39
 *
 * TODO fill me please!
 *
 * @see  http://mukis.de/pages/sbt-autoplugins-tutorial/
 */
object DockerLauncherPlugin extends AutoPlugin{

  object SettingsAndTasks {
    lazy val containers = SettingKey[Seq[ContainerInstanceDefinition]]("containers", "A list of docker containers to be launched")
    lazy val launchContainers = TaskKey[Boolean]("launch-containers", "Launches a docker definition.")
    lazy val stopContainers = TaskKey[Boolean]("stop-containers", "Stops a definition")
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
      val ops = for{
        container <- containers.value
      } yield launch(container,logger)
      ops.reduce(_ && _)
    },

    stopContainers := {
      println(s"stopping docker containers")
      val ops = for{
        container <- containers.value
      } yield launch(container,sbt.Keys.streams.value.log)
      ops.reduce(_ && _)
    }
  )


  private def launch(definition:ContainerInstanceDefinition,logger:Logger) : Boolean = {
    logger.info(s"launching docker definition $definition")
    OS.current match {
      case OS.Type.MacOs =>
        val docker = OS.MacOs.get(logger)
        val runningContainers = docker.ps()(logger)
        if(!runningContainers.contains(definition.container)){
          docker.run(definition)(logger)
        }else{
          logger.warn(s"definition ${definition.container} already running")
        }
    }
    true
  }

  private def stop(definition:ContainerInstanceDefinition) : Boolean = {
    //FIXME streams.value.log.info(s"stopping docker definition $definition")
    println(s"stopping docker definition $definition")
    true
  }



}

case class Container(repository:String,name:String)
case class ContainerInstanceDefinition(container:Container,tunneling:Seq[(Int,Int)]=Seq()){
  val command =  tunneling.map(p => s"-p ${p._1}:${p._2}").mkString(" ") + s" -P --name ${container.name} -d ${container.repository}"
}
case class ContainerInstance(container:Container,id:String, running:Boolean)

