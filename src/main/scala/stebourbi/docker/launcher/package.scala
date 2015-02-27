package stebourbi.docker

/**
 * User: slim
 * Date: 23/02/15
 * Time: 18:09
 *
 * TODO fill me please!
 */
package object launcher {
  implicit def toDefinition(container:(String,String)) : ContainerInstanceDefinition = new ContainerInstanceDefinition(container._1,container._2)
}
