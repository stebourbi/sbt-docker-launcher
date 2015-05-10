package stebourbi.docker.launcher

import sbt.Logger

import scala.sys.process.{Process, ProcessIO}

/**
 * User: slim
 * Date: 06/02/15
 * Time: 12:40
 *
 * TODO fill me please!
 */
object Shell {

  val NewLine = sys.props("line.separator")

  type CommandOutputHandler[T] = ( CommandOutput => T )

  def runCommand[T](command:String, handler:(CommandOutput=>T),env:Seq[(String,String)] = Seq())(implicit logger:Logger) : T = {

    val collector = new Collector()
    val pio : ProcessIO = new ProcessIO(_ => ()
      , stdout => { collector.stdOut = scala.io.Source.fromInputStream(stdout).getLines }
      , stderr => { collector.stdErr = scala.io.Source.fromInputStream(stderr).getLines } )

    val exitCode = Process(command,None,env:_*).run(pio).exitValue()

    handler(CommandOutput(collector.stdErr,collector.stdOut,exitCode))
  }

  case class CommandOutput(stdErr:Iterator[String],stdOut:Iterator[String], exitCode:Int)

  class Collector(var stdErr:Iterator[String]=null,var stdOut:Iterator[String]=null)

  class DefaultCommandOutputHandler(logger:Logger) extends CommandOutputHandler[Unit] {
    override def apply(output: CommandOutput): Unit = {
      //logger.error(output.stdErr.filterNot(_.stripMargin.trim.isEmpty).mkString(OS.NewLine))
      logger.debug(output.stdOut.filterNot(_.stripMargin.trim.isEmpty).mkString(NewLine))
      output.exitCode match {
        case 0 => ()
        case _ => {
          logger.error(output.stdErr.filterNot(_.stripMargin.trim.isEmpty).mkString(NewLine))
          sys.error(s"Command Exits with failure code ${output.exitCode}")
        }
      }
    }
  }




}

