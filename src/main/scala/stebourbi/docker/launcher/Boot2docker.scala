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
    runCommand("""boot2docker up""",new DefaultCommandOutputHandler(logger))(logger)
  }



  def dockerHostEnvVar(logger:Logger) : Seq[(String, String)] = {
    runCommand[Seq[(String, String)]]("""boot2docker shellinit""",new HostEnvVarCommandOutputHandler(logger)) (logger)
  }

  class  HostEnvVarCommandOutputHandler(logger:Logger) extends CommandOutputHandler[Seq[(String, String)]] {
    val EnvironmentVariableRegEx = "^[ ]*export ([A-Z_]*)=(.+)$".r

    override def apply(output: CommandOutput): Seq[(String, String)] = {
      output.stdOut.map(line => line match {
        case EnvironmentVariableRegEx(name,value) => {
          (name,value.trim)
        }
        case _ => {
          println(line.matches(EnvironmentVariableRegEx.pattern.pattern())+ " : " + line)
          ("","")
        }
      }) filterNot(_._1.isEmpty) toSeq
    }



  }




}
