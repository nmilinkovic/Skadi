package skadi.container

import scala.reflect.Manifest

trait BeanRepository {

  def containsBean(name: Symbol): Boolean

  def getBeansCount: Int = getAllBeanNames.size

  def getAllBeanNames(): Set[Symbol]

  def getBeanNamesForExactType[T](implicit m: Manifest[T]) : Set[Symbol]

  def getAssignableBeanNamesForType[T](implicit m: Manifest[T]): Set[Symbol]

}
