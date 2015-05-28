package stebourbi.docker.launcher

import org.scalatest.{FlatSpec, Matchers}

/**
 * ...
 */
class DockerPsStdOutHandlerTest extends FlatSpec with Matchers{

  it should "..." in {
    val regex = new DockerPsStdOutHandler

    val regexNotGood = "d0a0d161def3d405090d71b93165d48a1ed18ea72702e922a6382714b7ac39be   redis:latest        \"/entrypoint.sh redis-server\"   9 hours ago         Up 5 seconds        0.0.0.0:32774->6379/tcp   redis2"
    val regexNotGood2 = "00fb158ccc49fbca842939ff9ef97afb994d52e7aacfde5ce9c624d1881a8c4d   redis:latest        \"/entrypoint.sh redis-server\"   9 hours ago         Up 7 seconds        0.0.0.0:6379->6379/tcp    redis1,redis2/master"
    regexNotGood match{
      case regex.DockerPs(id,image,command,tail,status,name) => {

        name should equal("redis2")

      }
      case _ => assert(false)
    }

    regexNotGood2 match{
      case regex.DockerPs(id,image,command,tail,status,name) => {
        name should equal("redis1,redis2/master")

      }
      case _ => assert(false)
    }
  }

}
