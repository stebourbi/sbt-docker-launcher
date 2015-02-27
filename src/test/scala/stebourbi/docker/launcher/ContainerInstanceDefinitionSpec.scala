package stebourbi.docker.launcher

import org.scalatest.FlatSpec

/**
 * User: slim
 * Date: 06/02/15
 * Time: 19:24
 *
 * TODO fill me please!
 */
class ContainerInstanceDefinitionSpec extends FlatSpec {


  it must "translate definition with ports" in {
    val cmd = (new ContainerInstanceDefinition("repository","name") p (1,1) p (2,2) ).commandArguments

    assert(" -p 2:2 -p 1:1   -P -d  --name name repository" === cmd)
  }

  it must "translate definition without ports" in {
    val cmd = ContainerInstanceDefinition(Container("repository","name")).commandArguments

    assert("    -P -d  --name name repository" === cmd)
  }


  it must "implicitly convert definition dsl to instance" in {

    val definition: ContainerInstanceDefinition = ("repo","name")

    assert (ContainerInstanceDefinition(Container("repo","name")) === definition)
  }

  it must "implicitly convert definition with tunneling dsl to instance" in {

    val definition: ContainerInstanceDefinition = ("repo","name") p  (9090,9091) p (80,8080)

    assert (ContainerInstanceDefinition(Container("repo","name"),Seq((80,8080),(9090,9091))) === definition)
  }

  it must "translate defined environment vars" in {
    val cmd = new ContainerInstanceDefinition("repository","name") e ("VAR1","VAL1")

    assert("  -e 'VAR1=VAL1'  -P -d  --name name repository" === cmd.commandArguments)

  }

  it must "translate defined linked containers" in {
    val cmd = new ContainerInstanceDefinition("repository","name") link ("linked","alias")

    assert("   --link 'linked:alias' -P -d  --name name repository" === cmd.commandArguments)

  }

}
