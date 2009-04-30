package skadi.container

import scala.reflect.Manifest

import skadi.beans.Bean

/**
 * Provides methods for the retrieval, adding and removal of the beans from the
 * container, as well as accessing information about the beans that are
 * registered within the container.
 *
 * @author Nikola Milinkovic
 */
trait BeanRepository extends BeanEvaluator {

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
   * @throws BeanNotInstantiableException if the requested bean could not be
   *  instantiated
   * @throws ClassCastException if the bean instance cannot be cast to the given
   * type
   */
  def getBean[T](name: Symbol): T = {
    val bean = findBean(name)
    getInstance(bean).asInstanceOf[T]
  }


  /**
   * Optionally returns an instance of the bean with the given name if the bean
   * is found and can be cast to the desired type.
   * If not, an instance of <tt>None</tt>, will be returned.
   *
   * @param <T> type that the instance of the bean will be cast to
   *
   * @param name name of the bean that will be retrieved
   *
   * @return an instance of the bean, cast as <T>
   *
   */
  def getOptionalBean[T](name: Symbol): Option[T] = {
    if (containsBean(name)) Some(getBean[T](name))
    else None
  }


  /**
   * Returns <tt>true</tt> if the bean with the given name is defined within
   * the container, <tt>false</tt> otherwise.
   *
   * @param name
   *       name of the bean used to look it up
   * @return <tt>true</tt> if the bean is defined, <tt>false</tt> otherwise
   */
  def containsBean(name: Symbol): Boolean = context.contains(name)

  /**
   * Returns the total count of the beans that were defined by the user.
   *
   * @return the number of defined beans within the container
   */
  def getBeansCount: Int = context.size

  /**
   * Returns all the names of the beans defined within the container.
   *
   * @return a set of defined bean names
   */
  def getAllBeanNames(): Set[Symbol] = Set.empty ++ context.keySet

  /**
   * Returns a set of names of the beans that are implemented with
   * the supplied class.
   *
   * @param <T> implementing class
   *
   * @return a set of names of the beans that are implemented with the supplied
   * class
   */
  def getBeanNamesForExactType[T](implicit m: Manifest[T]) : Set[Symbol] = {
    getFilteredBeanNames(b => b.clazz == m.erasure)
  }

  /**
   * Returns a set of bean names that are assignable the supplied class.
   *
   * @param <T> class that the returned beans should be assignable to
   *
   * @return a set of bean names that are assignable to the class
   */
  def getAssignableBeanNamesForType[T](implicit m: Manifest[T]): Set[Symbol] = {
    getFilteredBeanNames(b => isAssignable(b.name, m.erasure))
  }

  private def getFilteredBeanNames(f: (Bean) => Boolean): Set[Symbol] = {
    val matchingBeans = context.values.filter(f)
    val names = matchingBeans.map(_.name)
    Set.empty ++ names
  }

}
