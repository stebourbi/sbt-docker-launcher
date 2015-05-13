package stebourbi.docker.launcher

import java.io.File

import sbt._
import Shell._
import stebourbi.docker.launcher.DockerInfo.{DockerInfoLinux, DockerInfoBoot2Docker}


/**
 * User: slim
 * Date: 06/02/15
 * Time: 10:30
 *
 * TODO fill me please!
 */

object Docker{
  def of(logger:Logger)  : Docker = {
    DockerInfo.dockerInfo match {
      case DockerInfoBoot2Docker(_) => {
        Boot2docker.up(logger)
        val env = Boot2docker.dockerHostEnvVar(logger)
        new OsxDocker(env)
      }
      case DockerInfoLinux(_) => {
        if(! DockerDaemon.isRunning()(logger)){
          sys.error("docker daemon is not running!")
        }
        new LinuxDocker
      }
      case _ => sys.error("your OS is not yet managed!")
    }
  }


}

sealed trait Docker{

  def rm(containers: Seq[ContainerInstance]) (implicit logger:Logger) : Unit

  def ps()(implicit logger:Logger) : Seq[ContainerInstance]

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance]

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) : Unit

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit

  def start(container:ContainerInstance)(implicit logger:Logger) : Unit
}


abstract class BaseDocker(dockerBin:DockerBin) extends Docker{

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) = {
    logger.info(s"docker run ${definition.commandArguments}")
    runCommand(s"${dockerBin.dockerExec} run ${definition.commandArguments}",new DefaultCommandOutputHandler(logger),dockerBin.envVars)

  }

  def ps()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand(s"${dockerBin.dockerExec} ps --no-trunc", new DockerPsStdOutHandler,dockerBin.envVars)
  }

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand(s"${dockerBin.dockerExec} ps --no-trunc -a", new DockerPsStdOutHandler,dockerBin.envVars)
  }

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker stop ${container}")
    runCommand(s"${dockerBin.dockerExec} stop ${container.id}", new DefaultCommandOutputHandler(logger),dockerBin.envVars)
  }

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit = {
    logger.info(s"docker unpause ${container}")
    runCommand(s"${dockerBin.dockerExec} unpause ${container.id}", new DefaultCommandOutputHandler(logger),dockerBin.envVars)
  }
  def start(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker start ${container}")
    runCommand(s"${dockerBin.dockerExec} start ${container.id}", new DefaultCommandOutputHandler(logger),dockerBin.envVars)
  }

  def rm(containers: Seq[ContainerInstance]) (implicit logger:Logger)  : Unit = {
    logger.info(s"docker start ${containers}")

    runCommand(s"${dockerBin.dockerExec} rm -f ${containers.map(_.id)}", new DefaultCommandOutputHandler(logger),dockerBin.envVars)
  }



}

class OsxDocker(env:Seq[(String,String)] = Seq()) extends BaseDocker(new OsxDockerBin(env))

class LinuxDocker extends BaseDocker(new LinuxDockerBin)


sealed trait DockerBin{
  def dockerExec : String
  def envVars : Seq[(String,String)]  = Seq()
}

class LinuxDockerBin extends DockerBin{
  override def dockerExec = "docker"
}

class OsxDockerBin(override val envVars:Seq[(String,String)] ) extends DockerBin{
  override def dockerExec = "docker"

}

class SudoerLinuxDockerBin extends LinuxDockerBin{
  override def dockerExec = "sudo docker"
}


class DockerPsStdOutHandler extends CommandOutputHandler[Seq[ContainerInstance]]{

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
