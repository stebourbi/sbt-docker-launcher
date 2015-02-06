package stebourbi.docker

import sbt.Logger

import scala.sys.process.{Process, ProcessIO}

/**
 * User: slim
 * Date: 05/02/15
 * Time: 21:18
 *
 * TODO fill me please!
 */
package object launcher {



  type ExecutionMessagesHandler[T] = (Iterator[String]=>T)
  type ResultHandler[T] = (Int=>T)

  val stdErr = new DefaultErrorMessageHandler

  @Deprecated
  def runCommand[T](command:String
                    , stdOut:ExecutionMessagesHandler[T] = new DefaultStandardOutputHandler
                    , resultHandler: ResultHandler[T] = new DefaultResultHandler )(implicit logger:Logger) : T = {
    val pio : ProcessIO = new ProcessIO(_ => ()
      , stdout => stdOut(scala.io.Source.fromInputStream(stdout).getLines)
      , stderr => stdErr(command,scala.io.Source.fromInputStream(stderr).getLines))

    val exitValue = Process(command).run(pio).exitValue()

    resultHandler(exitValue)

  }


  class DefaultStandardOutputHandler extends ExecutionMessagesHandler[Unit]{
    override def apply(v1: Iterator[String]): Unit = ()
  }

  class DefaultResultHandler extends ResultHandler[Unit] {
    override def apply(exitCode: Int): Unit = exitCode match {
      case 0 => ()
      case _ => sys.error(s"Command Exits with failure code $exitCode")
    }
  }

  class DefaultErrorMessageHandler  {
    def apply(command:String, messages: Iterator[String])(implicit logger:Logger) : Unit = {
      logger.error(messages.filterNot(_.stripMargin.trim.isEmpty).mkString(OS.NewLine))
      ()
    }
  }

}
