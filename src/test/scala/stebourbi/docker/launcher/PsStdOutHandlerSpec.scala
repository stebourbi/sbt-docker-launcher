package stebourbi.docker.launcher

import org.scalatest.FlatSpec
import sbt.ConsoleLogger
import Shell._

/**
 * User: slim
 * Date: 06/02/15
 * Time: 11:10
 *
 * TODO fill me please!
 */
class PsStdOutHandlerSpec extends FlatSpec {

  implicit val logger = ConsoleLogger.apply(System.out)
  val handler = new PsStdOutHandler

  it must "parse containers when exists" in {
    val stdOut = Seq("""CONTAINER ID        IMAGE                     COMMAND                CREATED             STATUS              PORTS                                            NAMES"""
                   , """edfb1b524bcc        carrefour/phenix:latest   "/bin/sh -c $KAFKA_H   13 hours ago        Up 42 minutes       0.0.0.0:2181->2181/tcp, 0.0.0.0:9092->9092/tcp   phenix-infra """).toIterator


    val containers = handler.apply(CommandOutput(null,stdOut,0))
    println(containers)


  }

}
