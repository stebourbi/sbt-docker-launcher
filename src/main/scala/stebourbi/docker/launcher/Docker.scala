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
      case _ => sys.error("your OS is not managed!")
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
    runCommand("docker ps", new PsStdOutHandler,env)
  }

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand("docker ps -a", new PsStdOutHandler,env)
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
  override def apply(output: CommandOutput): Seq[ContainerInstance] = { //TODO refactor with a more solid regex
    output.stdOut.toList.tail.map( line =>  //FIXME buggy code
    {
      val tokens = line.split("  ").map(_.trim).filterNot(_.isEmpty)
      if(tokens.length == 7){
        val status = tokens(4) match {
          case m:String if m.contains("paused") => ContainerStatus.Paused
          case m:String if m.startsWith("Up") => ContainerStatus.Running
          case m:String if m.startsWith("Exited") => ContainerStatus.Stopped
        }
        ContainerInstance(Container(tokens(1),tokens(6)),tokens(0),status)
      } else if (tokens.length==6){
        val status = tokens(4) match {
          case m:String if m.contains("paused") => ContainerStatus.Paused
          case m:String if m.startsWith("Up") => ContainerStatus.Running
          case m:String if m.startsWith("Exited") => ContainerStatus.Stopped
        }
        ContainerInstance(Container(tokens(1),tokens(5)),tokens(0),status)
      }
      else {
        ContainerInstance(Container(tokens(1),"???"),tokens(0),ContainerStatus.Stopped)
      }})
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

