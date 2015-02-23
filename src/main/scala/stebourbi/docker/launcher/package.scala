package stebourbi.docker

/**
 * User: slim
 * Date: 23/02/15
 * Time: 18:09
 *
 * TODO fill me please!
 */
package object launcher {

  implicit def toDefinition(container:(String,String)) : ContainerInstanceDefinition = ContainerInstanceDefinition(Container(container._1,container._2))
  implicit def toDefinition(container:(String,String),tunneling:Seq[(Int,Int)]) : ContainerInstanceDefinition = ContainerInstanceDefinition(Container(container._1,container._2),tunneling)
}
