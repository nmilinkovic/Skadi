package skadi.container

/**
 * Performs different evaluations on a bean that is defined within the
 * application context.
 *
 * @author Nikola Milinkovic
 */
private[container] trait BeanEvaluator {

  //should throw an exception if the bean is not there
  def isSingleton(name: Symbol): Boolean

  //should throw an exception if the bean is not there
  def isPrototype(name: Symbol): Boolean

  //should throw an exception if the bean is not there
  def isAssignable(name: Symbol, clazz: Class[_]): Boolean

  //should throw an exception if the bean is not there
  def isLazy(name: Symbol): Boolean

  //should throw an exception if the bean is not there
  def getType(name: Symbol): Class[_]

  def getOptionalType(name: Symbol): Option[Class[_]]

}
