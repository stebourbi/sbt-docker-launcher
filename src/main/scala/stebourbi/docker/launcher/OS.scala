package stebourbi.docker.launcher

import sbt.Logger

import scala.sys.process.Process
import stebourbi.docker.launcher.OS.Type.OsType

import scala.sys.process.ProcessIO

/**
 * User: slim
 * Date: 05/02/15
 * Time: 17:08
 *
 * TODO fill me please!
 */
object OS {
  val NewLine = System.getProperty("line.separator"	)

  object Type extends Enumeration{
    type OsType = Value
    val MacOs,Linux,Windows = Value
  }

  val current : OsType = System.getProperty("os.name") match {
    case u:String if u.startsWith("Mac") => Type.MacOs
    case u:String if u.startsWith("Windows") => Type.Windows
    case _ => Type.Linux
  }


  object MacOs{
    def get(logger:Logger) : Docker = {
      Boot2docker.up(logger)
      val env = Boot2docker.dockerHostEnvVar(logger)
      new OsxDocker(env)
    }
  }


  object Linux{
    def get(logger:Logger) : Docker = {
      if(! DockerDaemon.isRunning()(logger)){
        sys.error("docker daemon is not running!")
      }
      new LinuxDocker
    }
  }

  
  
  def setDockerHostSystemEnv(line:String) : Unit = {
    val message = line.stripMargin
    if(!message.isEmpty && message.startsWith("export DOCKER_HOST")){
      Process(line).!
    }
  }

}


