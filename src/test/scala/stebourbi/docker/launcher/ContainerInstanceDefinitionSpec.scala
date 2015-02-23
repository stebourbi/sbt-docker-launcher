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
    val cmd = ContainerInstanceDefinition(Container("repository","name"),Seq((1,1),(2,2))).commandArguments

    assert("-p 1:1 -p 2:2 -P --name name -d repository" === cmd)
  }

  it must "translate definition without ports" in {
    val cmd = ContainerInstanceDefinition(Container("repository","name")).commandArguments

    assert(" -P --name name -d repository" === cmd)
  }


  it must "implicitly convert definition dsl to instance" in {

    val definition: ContainerInstanceDefinition = ("repo","name")

    assert (ContainerInstanceDefinition(Container("repo","name")) === definition)
  }

  it must "implicitly convert definition with tunneling dsl to instance" in {

    val definition: ContainerInstanceDefinition = ("repo","name") ~> ( (9090,9091) , (80,8080))

    assert (ContainerInstanceDefinition(Container("repo","name"),Seq( (9090,9091) , (80,8080))) === definition)
  }
}
