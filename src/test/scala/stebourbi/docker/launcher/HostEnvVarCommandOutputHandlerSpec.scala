package stebourbi.docker.launcher

import org.scalatest.{Matchers, FlatSpec}
import sbt.ConsoleLogger
import stebourbi.docker.launcher.Boot2docker.HostEnvVarCommandOutputHandler
import stebourbi.docker.launcher.Shell.CommandOutput

/**
 * User: slim
 * Date: 06/02/15
 * Time: 07:52
 *
 * TODO fill me please!
 */
class HostEnvVarCommandOutputHandlerSpec extends FlatSpec with Matchers{

  val logger = ConsoleLogger.apply(System.out)



  it must "find env var to set in boot2docker v < 1.5 up output" in {
    val boot2docker_up_out = List("""Waiting for VM and Docker daemon to start..."""
                               ,""".."""
                               ,"""Started."""
                               ,"\n"
                               ,"""To connect the Docker client to the Docker daemon, please set:"""
                               ,"""    export DOCKER_HOST=tcp://192.168.59.103:2375"""
                               ,"\n"
                               ).toIterator

    val envVars = new HostEnvVarCommandOutputHandler(logger).apply(CommandOutput(null,boot2docker_up_out,0))
    envVars should contain only(("DOCKER_HOST","tcp://192.168.59.103:2375"))
  }


  it must "find env var to set in boot2docker v > 1.5 up output" in {
    val boot2docker_up_out = List( """Waiting for VM and Docker daemon to start..."""
      , """.o                                                                       """
      , """Started.                                                                 """
      , """Writing /Users/slim/.boot2docker/certs/boot2docker-vm/ca.pem             """
      , """Writing /Users/slim/.boot2docker/certs/boot2docker-vm/cert.pem           """
      , """Writing /Users/slim/.boot2docker/certs/boot2docker-vm/key.pem            """
      , """                                                                         """
      , """To connect the Docker client to the Docker daemon, please set:           """
      , """    export DOCKER_HOST=tcp://192.168.59.103:2376                         """
      , """    export DOCKER_CERT_PATH=/Users/slim/.boot2docker/certs/boot2docker-vm"""
      , """    export DOCKER_TLS_VERIFY=1                                           """
      , "\n").iterator

    val envVars = new HostEnvVarCommandOutputHandler(logger).apply(CommandOutput(null, boot2docker_up_out, 0))

    envVars should contain allOf (("DOCKER_HOST", "tcp://192.168.59.103:2376"), ("DOCKER_CERT_PATH", "/Users/slim/.boot2docker/certs/boot2docker-vm"), ("DOCKER_TLS_VERIFY", "1"))



  }
  it must "find nothing when env var already set" in {
    val boot2docker_up_out = List("""Waiting for VM and Docker daemon to start...  """
    ,"""  .o                                                                       """
    ,"""Started.                                                                   """
    ,"""  Writing /Users/slim/.boot2docker/certs/boot2docker-vm/ca.pem             """
    ,"""Writing /Users/slim/.boot2docker/certs/boot2docker-vm/cert.pem             """
    ,"""Writing /Users/slim/.boot2docker/certs/boot2docker-vm/key.pem              """
    ,"""Your environment variables are already set correctly.                   """
    , "\n"
    ).toIterator

    val envVars = new HostEnvVarCommandOutputHandler(logger).apply(CommandOutput(null,boot2docker_up_out,0))

    envVars should be (empty)

  }
}


