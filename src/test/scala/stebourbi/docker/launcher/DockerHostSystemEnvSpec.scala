package stebourbi.docker.launcher

import org.scalatest.FlatSpec
import sbt.ConsoleLogger
import stebourbi.docker.launcher.Boot2docker.DockerHostSystemEnv

/**
 * User: slim
 * Date: 06/02/15
 * Time: 07:52
 *
 * TODO fill me please!
 */
class DockerHostSystemEnvSpec extends FlatSpec {

  val logger = ConsoleLogger.apply(System.out)



  it must "find env var to set in boot2docker up output" in {
    val boot2docker_up_out = List("""Waiting for VM and Docker daemon to start..."""
                               ,""".."""
                               ,"""Started."""
                               ,"\n"
                               ,"""To connect the Docker client to the Docker daemon, please set:"""
                               ,"""    export DOCKER_HOST=tcp://192.168.59.103:2375"""
                               ,"\n"
                               ).toIterator

    val command = new DockerHostSystemEnv(logger).findExportCommand(boot2docker_up_out)
    assert(command.isDefined)
    assert("tcp://192.168.59.103:2375" === command.get)

  }

  it must "find nothing when env var already set" in {
    val boot2docker_up_out = List("""Waiting for VM and Docker daemon to start..."""
      ,""".."""
      ,"""Started."""
      ,"\n"
      ,"""Your DOCKER_HOST env variable is already set correctly."""
      ,"\n"
    ).toIterator

    val command = new DockerHostSystemEnv(logger).findExportCommand(boot2docker_up_out)
    assert(command.isEmpty)

  }
}
