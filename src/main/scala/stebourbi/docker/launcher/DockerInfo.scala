package stebourbi.docker.launcher

import java.io.File

import sbt._

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

    System.getProperty("os.name") match {
      case u : String if u.startsWith("Windows") => val boot2DockerIp = Process("boot2docker ip").lines.head
          val dockerInfoValue = DockerInfoBoot2Docker(boot2DockerIp)
          dockerInfoValue
      case _ => {
        val hasBoot2docker = "which boot2docker" #> file("/dev/null") ! new FileProcessLogger(new File("/dev/null"))
        //with boot2docker
        if (hasBoot2docker == 0) {
          val boot2DockerIp = Process("boot2docker ip").lines.head
          val dockerInfoValue = DockerInfoBoot2Docker(boot2DockerIp)
          dockerInfoValue
        } else {
          val info = "ifconfig" #| "grep -A 1 docker0" #| "tail -1" #|
            Seq("sed", "-e", "s/[ ]*inet \\(a[d]\\{1,2\\}r:\\)\\{0,1\\}\\([0-9.]*\\) .*/\\2/")
          DockerInfoLinux(info.lines.head)
        }
      }

    }


  }

  lazy val dockerInfo = createDockerInfo()
}






