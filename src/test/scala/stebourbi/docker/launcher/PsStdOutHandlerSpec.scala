package stebourbi.docker.launcher

import org.scalatest.{Matchers, FlatSpec}
import sbt.ConsoleLogger
import Shell._

/**
 * User: slim
 * Date: 06/02/15
 * Time: 11:10
 *
 * TODO fill me please!
 */
class PsStdOutHandlerSpec extends FlatSpec with Matchers {

  implicit val logger = ConsoleLogger.apply(System.out)
  val handler = new PsStdOutHandler



  it must "parse containers when exists | without ports tuneling" in {
    val stdOut = Seq("""CONTAINER ID        IMAGE                     COMMAND                CREATED             STATUS              PORTS                                            NAMES"""
      , """6ee921aad94b337329a0c59f24e644640ce75dc1bb3ef46f2925e318716bab59   carrefour/phenix:latest            "/bin/sh -c $KAFKA_HOME/run-kafka.sh"                                                                                                                                      6 days ago          Exited (137) 3 hours ago                                                     phenix-infra                          """).iterator

    val containers = handler.apply(CommandOutput(null,stdOut,0))

    containers should contain only ContainerInstance(Container("carrefour/phenix:latest","phenix-infra"),"6ee921aad94b337329a0c59f24e644640ce75dc1bb3ef46f2925e318716bab59",ContainerStatus.Stopped)

  }


  it must "parse containers when exists | with ports tuneling" in {
    val stdOut = Seq("""CONTAINER ID        IMAGE                     COMMAND                CREATED             STATUS              PORTS                                            NAMES"""
      , """d240bf8fadc6f71b3d03b92ff6100f4376a2ec06e2e9eec1109915118d56f955   redis:latest                       "/entrypoint.sh redis-server"                                                                                                                                              6 days ago          Up 18 minutes (Paused)      0.0.0.0:49153->6379/tcp                          myredis                                """).iterator

    val containers = handler.apply(CommandOutput(null,stdOut,0))

    containers should contain only ContainerInstance(Container("redis:latest","myredis"),"d240bf8fadc6f71b3d03b92ff6100f4376a2ec06e2e9eec1109915118d56f955",ContainerStatus.Paused)

  }


  it must "get nothing if none" in {
    val stdOut = Seq("""CONTAINER ID        IMAGE                     COMMAND                CREATED             STATUS              PORTS                                            NAMES"""
      ).iterator

    val containers = handler.apply(CommandOutput(null,stdOut,0))

    containers should be (empty)

  }

}
