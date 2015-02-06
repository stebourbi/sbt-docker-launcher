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
    lazy val containers = SettingKey[Seq[String]]("containers", "A list of docker containers to be launched")
    lazy val launchContainers = TaskKey[Boolean]("launch-containers", "Launches a docker container.")
    lazy val stopContainers = TaskKey[Boolean]("stop-containers", "Stops a container")
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


  private def launch(container:String,logger:Logger) : Boolean = {
    logger.info(s"launching docker container $container")
    val name = "phenix-infra"
    val repo = "carrefour/phenix"

    OS.current match {
      case OS.Type.MacOs => {

        val docker = OS.MacOs.get(logger)

        val runningContainers = docker.ps()(logger)



        //Process(s"""docker rm $name""",None,Seq(("DOCKER_HOST",systemEnv)):_*).!

        //Process(s"""docker run -p 9092:9092 -p 2181:2181 -P --name $name -d $repo""",None,Seq(("DOCKER_HOST",systemEnv)):_*).!

      }
    }

    true
  }

  private def stop(container:String) : Boolean = {
    //FIXME streams.value.log.info(s"stopping docker container $container")
    println(s"stopping docker container $container")
    true
  }



}
