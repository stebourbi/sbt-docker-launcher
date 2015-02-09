package stebourbi.docker.launcher

import sbt.Logger
import Shell._

/**
 * User: slim
 * Date: 06/02/15
 * Time: 10:30
 *
 * TODO fill me please!
 */

class Docker(os:OS.Type.OsType,env:Seq[(String,String)] = Seq()){

  def run(definition:ContainerInstanceDefinition)(implicit logger:Logger) = {
    logger.info(s"docker run ${definition.command}")
    runCommand2(s"docker run ${definition.command}",env,new DefaultCommandOutputHandler(logger))
  }


  def ps()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand2("docker ps",env, new PsStdOutHandler)
  }

  def psAll()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand2("docker ps -a",env, new PsStdOutHandler)
  }

  def stop(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker stop ${container}")
    runCommand2(s"docker stop ${container.id}",env, new DefaultCommandOutputHandler(logger))
  }

  def unpause(container: ContainerInstance)  (implicit logger:Logger) : Unit = {
    logger.info(s"docker unpause ${container}")
    runCommand2(s"docker unpause ${container.id}",env, new DefaultCommandOutputHandler(logger))
  }
  def start(container:ContainerInstance)(implicit logger:Logger) : Unit = {
    logger.info(s"docker start ${container}")
    runCommand2(s"docker start ${container.id}",env, new DefaultCommandOutputHandler(logger))
  }

}

class PsStdOutHandler extends CommandOutputHandler[Seq[ContainerInstance]]{
  override def apply(output: CommandOutput): Seq[ContainerInstance] = { //TODO refactor with a more solid regex
    output.stdOut.toList.tail.map( line =>
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


