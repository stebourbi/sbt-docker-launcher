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

  def run() = {}


  def ps()(implicit logger:Logger) : Seq[ContainerInstance] = {
    runCommand2("docker ps",env, new PsStdOutHandler)
  }


}

class PsStdOutHandler extends CommandOutputHandler[Seq[ContainerInstance]]{
  override def apply(output: CommandOutput): Seq[ContainerInstance] = {
    output.stdOut.toList.tail.map( line =>
    {
      val tokens = line.split("  ").map(_.trim).filterNot(_.isEmpty)
      ContainerInstance(Container(tokens(1),tokens(6)),tokens(0),tokens(4).contains("Up"))
    }
    )
  }
}

case class Container(repository:String,name:String)
case class ContainerInstance(container:Container,id:String, running:Boolean)

