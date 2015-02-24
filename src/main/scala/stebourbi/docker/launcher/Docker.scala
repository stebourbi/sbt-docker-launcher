package stebourbi.docker.launcher

import sbt.Logger
import Shell._
import stebourbi.docker.launcher.OS.{Linux, MacOs}

/**
 * User: slim
 * Date: 06/02/15
 * Time: 10:30
 *
 * TODO fill me please!
 */

object Docker{
  def of(logger:Logger)  : Docker = {
    OS.current match {
      case OS.Type.MacOs => MacOs.get(logger)
      case OS.Type.Linux => Linux.get(logger)
      case _ => sys.error("your OS is not yet managed!")
    }
  }
}

sealed trait Docker{

  def ps()(implicit logger:Logger) : Seq[ContainerInstance]

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance]

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) : Unit

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit

  def start(container:ContainerInstance)(implicit logger:Logger) : Unit
}

class OsxDocker(env:Seq[(String,String)] = Seq()) extends Docker{

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) = {
    logger.info(s"docker run ${definition.commandArguments}")
    runCommand(s"docker run ${definition.commandArguments}",new DefaultCommandOutputHandler(logger),env)
  }


  def ps()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand("docker ps --no-trunc", new PsStdOutHandler,env)
  }

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand("docker ps --no-trunc -a", new PsStdOutHandler,env)
  }

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker stop ${container}")
    runCommand(s"docker stop ${container.id}", new DefaultCommandOutputHandler(logger),env)
  }

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit = {
    logger.info(s"docker unpause ${container}")
    runCommand(s"docker unpause ${container.id}", new DefaultCommandOutputHandler(logger),env)
  }
  def start(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker start ${container}")
    runCommand(s"docker start ${container.id}", new DefaultCommandOutputHandler(logger),env)
  }

}

class PsStdOutHandler extends CommandOutputHandler[Seq[ContainerInstance]]{

  val DockerPs = "^(\\w+)\\s+((\\w+/){0,1}\\w+(:\\w+){0,1})\\s+(\"[^\"]+\")\\s+.*((Exited|Up).*)[  ][ ]*(.*){0,1}\\s([\\w\\d-/]+)\\s*$".r

  override def apply(output: CommandOutput): Seq[ContainerInstance] = {
    output.exitCode match {
      case 0 if !output.stdOut.isEmpty  => {
        extractContainers(output.stdOut).filterNot(_.equals(ContainerInstance.Unknown))
      }
      case _ => sys.error(s"got error exit code : $output")
    }

  }

  def extractContainers(output: Iterator[String]) : Seq[ContainerInstance] = {
    output.toList.map( line =>  line match {
      case DockerPs(id,image,_,_,command,tail,status,_,name) => ContainerInstance(Container(image,name),id,ContainerStatus.from(tail))
      case _ => ContainerInstance.Unknown
    }
    )
  }
}


class LinuxDocker extends Docker {

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) = {
    logger.info(s"docker run ${definition.commandArguments}")
    runCommand(s"sudo docker run ${definition.commandArguments}",new DefaultCommandOutputHandler(logger))
  }


  def ps()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand("sudo docker ps", new PsStdOutHandler)
  }

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand("sudo docker ps -a", new PsStdOutHandler)
  }

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker stop ${container}")
    runCommand(s"sudo docker stop ${container.id}", new DefaultCommandOutputHandler(logger))
  }

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit = {
    logger.info(s"docker unpause ${container}")
    runCommand(s"sudo docker unpause ${container.id}", new DefaultCommandOutputHandler(logger))
  }
  def start(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker start ${container}")
    runCommand(s"sudo docker start ${container.id}", new DefaultCommandOutputHandler(logger))
  }

}

