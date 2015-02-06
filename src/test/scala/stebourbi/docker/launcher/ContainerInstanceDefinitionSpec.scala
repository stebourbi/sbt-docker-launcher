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
    val cmd = ContainerInstanceDefinition(Container("repository","name"),Seq((1,1),(2,2))).command

    assert("-p 1:1 -p 2:2 -P --name name -d repository" === cmd)
  }

  it must "translate definition without ports" in {
    val cmd = ContainerInstanceDefinition(Container("repository","name")).command

    assert(" -P --name name -d repository" === cmd)
  }

}
