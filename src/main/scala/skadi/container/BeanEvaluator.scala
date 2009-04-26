package skadi.container

/**
 * Performs different evaluations on a bean that is defined within the
 * container.
 *
 * @author Nikola Milinkovic
 */
trait BeanEvaluator {

  /**
   * Returns <tt>true</tt> if the bean with the supplied name is scoped as singleton,
   * <tt>false</tt> otherwise.
   *
   * @param name
   *       name of the bean that is being looked up
   * @return <tt>true</tt> if the bean is scoped as singleton
   *
   * @throws BeanNotFoundException
   */
  def isSingleton(name: Symbol): Boolean

  /**
   * Returns <tt>true</tt> if the bean with the supplied name is scoped as prototype,
   * <tt>false</tt> otherwise.
   *
   * @param name
   *       name of the bean that is being looked up
   * @return <tt>true</tt> if the bean is scoped as prototype
   *
   * @throws BeanNotFoundException
   */
  def isPrototype(name: Symbol): Boolean

  /**
   * Returns <tt>true</tt> if the bean with the supplied name is assignable to
   * the supplied class, <tt>false</tt> otherwise.
   *
   * @param name
   *       name of the bean that is being looked up
   * @param clazz
   *     class that this beans should be assignable to
   * @return <tt>true</tt> if the bean is assignable to the given class
   *
   * @throws BeanNotFoundException
   */
  def isAssignable(name: Symbol, clazz: Class[_]): Boolean

  /**
   * Returns <tt>true</tt> if the bean with the supplied name is loaded lazily
   * by the container, <tt>false</tt> otherwise.
   *
   * @param name
   *       name of the bean that is being looked up
   * @return <tt>true</tt> if the bean is loaded lazily
   *
   * @throws BeanNotFoundException
   */
  def isLazy(name: Symbol): Boolean

  /**
   * Returns the implementing class of the bean with the supplied name.
   *
   * @param name
   *       name of the bean that is being looked up
   * @return implementing class of the bean
   *
   * @throws BeanNotFoundException
   */
  def getType(name: Symbol): Class[_]

  /**
   * Optionally returns the implementing class of the bean with the supplied
   * name if the bean is defined.
   * Will return <tt>None</tt> if the bean is not found.
   *
   * @param name
   *       name of the bean that is being looked up
   *
   * @return <tt>Some</tt> implementing class of the bean if it is defined,
   * <tt>None</tt> otherwise
   */
  def getOptionalType(name: Symbol): Option[Class[_]]

}
