package skadi.container

import skadi.beans.FactoryBean;
import skadi.container.processing.InstanceProcessor

trait InstanceFactory {

    //should throw an exception if the bean is not there
  def getBean[T](name: Symbol): T

  def getOptionalBean[T](name: Symbol): Option[T]

  //should throw an exception if the bean is not there
  def createBeanWithConstructorArgs[T](name: Symbol, args: Any*): T

  // should throw an exception if the bean is not there
  def createBeanWithInjectables[T](name: Symbol, injectables: (Any, Symbol)*): T

  //should throw an exception if the bean is not there
  def createBean[T](name: Symbol, args: Seq[Any], injectables: Seq[(Any, Symbol)]): T

  private[container] def init(beans: Seq[FactoryBean]): Unit

  protected def instanceProcessors: List[InstanceProcessor]

}
