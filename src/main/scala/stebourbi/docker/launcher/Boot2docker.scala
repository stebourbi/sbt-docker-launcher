package stebourbi.docker.launcher
import sbt._
import Shell._

/**
 * User: slim
 * Date: 05/02/15
 * Time: 21:19
 *
 * TODO fill me please!
 */
object Boot2docker {

  def up(logger:Logger) = {
    runCommand[Unit]("""boot2docker up""")(logger)
  }



  def dockerHostEnvVar(logger:Logger) : Option[(String,String)] = {
    runCommand2[Option[(String,String)]]("""boot2docker shellinit""",Seq(),new HostEnvVarCommandOutputHandler(logger)) (logger)
  }

  class  HostEnvVarCommandOutputHandler(logger:Logger) extends CommandOutputHandler[Option[(String,String)]] {
    override def apply(output: CommandOutput): Option[(String, String)] = {
      findExportCommand(output.stdOut) match {
        case Some(host) => {
          Some(("DOCKER_HOST",host))
        }
        case None => {
          logger.warn("DOCKER_HOST system env not set!")
           None
        }
      }
    }

    def findExportCommand(messages: Iterator[String]):Option[String] = messages.find(_.contains("export DOCKER_HOST")).map(_.stripPrefix("    export DOCKER_HOST="))//TODO refactor!
  }


  @Deprecated
  class DockerHostSystemEnv(logger:Logger) extends ExecutionMessagesHandler[Unit] {//TODO refactor can have return
    override def apply(messages: Iterator[String]): Unit = {
      findExportCommand(messages) match {
        case Some(exportMessage) => {
          System.setProperty("DOCKER_HOST",exportMessage)
        }
        case None => logger.warn("DOCKER_HOST system env not set!")
      }
    }
    def findExportCommand(messages: Iterator[String]):Option[String] = messages.find(_.contains("export DOCKER_HOST")).map(_.stripPrefix("    export DOCKER_HOST="))//TODO refactor!
  }


}
