package skadi.container

import skadi.beans.FactoryBean;
import skadi.container.processing.InstanceProcessor

/**
 * Defines methods for the on-demand retrieval and creation of the defined beans.
 *
 * @author Nikola Milinkovic
 */
trait InstanceFactory {

  /**
   * Returns the instance of the bean with the given name. If the bean is scoped
   * as singleton, it will return the instance shared with other beans. If it is
   * scoped as prototype, a new instance of the bean will be created and
   * returned.
   *
   * @param <T> type that the instance of the bean will be cast to
   *
   * @param name name of the bean that will be retrieved
   *
   * @return an instance of the bean, cast as <T>
   *
   * @throws BeanNotFoundException if the bean with the given name is not found
   *
   * @throws ClassCastException if the bean instance cannot be cast to the given
   * type
   */
  def getBean[T](name: Symbol): T

  /**
   * Optionally returns an instance of the bean with the given name if the bean
   * is found. If not, an instance of <tt>None</tt>, will be returned.
   *
   * @param <T> type that the instance of the bean will be cast to
   *
   * @param name name of the bean that will be retrieved
   *
   * @return an instance of the bean, cast as <T>
   *
   * @throws ClassCastException if the bean instance cannot be cast to the given
   * type
   */
  def getOptionalBean[T](name: Symbol): Option[T]

  // should throw an exception if the bean is not there
  // unsupported yet
  def createBeanWithConstructorArgs[T](name: Symbol, args: Any*): T

  // should throw an exception if the bean is not there
  // unsupported yet
  def createBeanWithInjectables[T](name: Symbol, injectables: (Any, Symbol)*): T

  //should throw an exception if the bean is not there
  // unsupported yet
  def createBean[T](name: Symbol, args: Seq[Any], injectables: Seq[(Any, Symbol)]): T

  private[container] def init(beans: Seq[FactoryBean]): Unit

  protected def instanceProcessors: List[InstanceProcessor]

}
