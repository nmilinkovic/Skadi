package skadi.beans

/**
 * Base bean class, contains properties that are common to all beans.
 *
 * @author Nikola Milinkovic
 */
private[skadi] abstract class AbstractBean {

  /**
   * Unique name of the bean, used to identify the bean within the application
   * context. Initialized with an UUID, in case the bean is not named explicitly
   * by the user.
   */
  var name = Symbol(java.util.UUID.randomUUID.toString)

  /**
   * Actual class of this bean.
   */
  var clazz: Class[_] = null

  /**
   * Constructor arguments that will be used to create an instance of this bean
   * when necessary.
   */
  var args: List[Any] = Nil

  /**
   * Values that will be injected after creation of the instance via setter
   * methods.
   */
  var injectables: List[(Any, Symbol)] = Nil

  /**
   * Initialization method that will be invoked on the instance of this bean
   * after it has been constructed and injected with setter dependencies.
   */
  var initMethod: Symbol = null

  /**
   * Optional arguments that go in the initialization method.
   */
  var initArgs: List[Any] = Nil

  /**
   * Scope of this bean, defaults to <tt>Singleton</tt>.
   */
  var scope = Scope.Singleton

  /**
   * Is the bean loaded lazily, defaults to false.
   */
  var lazyBean = false

  /**
   * Determines if the bean was named explicitly by the user.
   */
  var beanNamed = false

}
