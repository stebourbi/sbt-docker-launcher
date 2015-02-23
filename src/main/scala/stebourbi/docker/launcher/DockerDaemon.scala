package stebourbi.docker.launcher

import Shell._
import sbt.Logger

/**
 * User: slim
 * Date: 21/02/15
 * Time: 19:45
 *
 * TODO fill me please!
 */
object DockerDaemon {

  def isRunning()(implicit logger:Logger) : Boolean = {
    runCommand("service docker status", new ServiceRunningHandler)
  }

}


class ServiceRunningHandler extends CommandOutputHandler[Boolean]{
  override def apply(output: CommandOutput): Boolean = output.exitCode == 0
}