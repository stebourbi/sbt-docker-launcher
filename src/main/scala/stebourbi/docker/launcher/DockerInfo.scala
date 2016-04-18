package stebourbi.docker.launcher

import java.io.File

import com.spotify.docker.client.{DockerClient, DefaultDockerClient}
import sbt._

import scala.util.Try

/**
 * Information on docker Ip host,...
 */
object DockerInfo {


  //TODO find better place and way
  abstract class DockerInfo(val host: String, val dockerHost: String)

  case class DockerInfoLinux(override val dockerHost: String) extends DockerInfo("127.0.0.1", dockerHost)

  case class DockerInfoBoot2Docker(val boot2dockerip: String) extends DockerInfo(boot2dockerip, boot2dockerip)


  def createDockerInfo(): DockerInfo = {
    import scala.sys.process._
    val docker = DefaultDockerClient.fromEnv().build()
    try {
      System.getProperty("os.name") match {
        case u: String if u.equalsIgnoreCase("linux") =>
          val info = "ifconfig" #| "grep -A 1 docker0" #| "tail -1" #|
            Seq("sed", "-e", "s/[ ]*inet \\(a[d]\\{1,2\\}r:\\)\\{0,1\\}\\([0-9.]*\\) .*/\\2/")
          DockerInfoLinux(info.lines.headOption.getOrElse("127.0.0.1"))
        case _ => DockerInfoBoot2Docker(docker.getHost)
      }
    } finally {
      docker.close()
    }
  }

  lazy val dockerInfo = createDockerInfo()
}






