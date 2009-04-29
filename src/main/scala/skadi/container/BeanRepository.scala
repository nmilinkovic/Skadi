package skadi.container

import scala.reflect.Manifest

import skadi.beans.FactoryBean;

/**
 * Defines methods for accessing beans that are defined in the container.
 *
 * @author Nikola Milinkovic
 */
trait BeanRepository extends BeanEvaluator {

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
  def getBeansCount: Int = getAllBeanNames.size

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

  private def getFilteredBeanNames(f: (FactoryBean) => Boolean): Set[Symbol] = {
    val matchingBeans = context.values.filter(f)
    val names = matchingBeans.map(_.name)
    Set.empty ++ names
  }

}
