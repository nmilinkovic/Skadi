package skadi.beans

/**
 * Enumerates all the lifecycle scopes that can be assigned to a bean instance
 * within the container.
 *
 * @author Nikola Milinkovic
 */
private[skadi] object Scope extends Enumeration {

  /**
   * Bean is scoped as a singleton, meaning that only one instance of the bean
   * is created and is shared among the beans that require it.
   */
  val Singleton = Value

  /**
   * Bean is scoped as prototype, meaning that every bean that depends on this
   * bean will receive a new instance that is unique for that bean.
   */
  val Prototype = Value

}
