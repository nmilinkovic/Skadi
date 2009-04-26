package skadi.beans

/**
 * Wrapper class for the user defined bean. Contains the additional attributes
 * required for the application context).
 *
 * @author Nikola Milinkovic
 */
private[skadi] class FactoryBean(bean: Bean) extends AbstractBean {

  /**
   * Constructor that is used to instantiate the target class.
   */
  var constructor: java.lang.reflect.Constructor[_] = null

  /**
   * The instance of the target class, created using the given constructor
   * arguments and injected with the setter arguments.
   */
  var instance: Any = null

  /**
   * Determines if the target class is abstract or not.
   */
  var abstractClass = false

  // initialize basic values
  this.name = bean.name
  this.clazz = bean.clazz
  this.args = bean.args
  this.injectables = bean.injectables
  this.scope = bean.scope
  this.lazyBean = bean.lazyBean  

}
