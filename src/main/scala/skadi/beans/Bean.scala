package skadi.beans

import scala.collection.mutable
import scala.reflect.Manifest

/**
 * Use this class to define a single bean in the container. The container will
 * create an instance of this bean, inject it with declared dependencies and in
 * turn will inject it to other beans that depend on it. An example bean:
 *
 * <pre>
 *   new Bean named 'userService
 *   implementedWith classOf[com.sample.app.UserServiceImpl]
 *   usingConstructorArgs 'userDao
 *   inject 100 -> 'maxPosts
 *   initializeWith('init, 5)
 * </pre>
 *
 */
class Bean extends AbstractBean {

  /**
   * Define the name for this bean using this method. The name must be unique
   * for each bean as it used to resolve dependencies between beans. If a bean
   * name is not defined it cannot be injected to other beans as a dependency.
   *
   * @param name
   *       unique name for this bean
   * @return the instance of this bean
   */
  def named(name: Symbol): Bean = {
    this.name = name
    beanNamed = true
    this
  }

  /**
   * The implementing class of this bean is declared using this method. This
   * method MUST be invoked when defining a bean.
   *
   * @param clazz
   *       this bean's defining class
   * @return the instance of this bean
   */
  def implementedWith(clazz: Class[_]): Bean = {
    this.clazz = clazz
    this
  }

  /**
   * Any parameters that are required by the constructor that will be used to
   * create an instance of the bean should be defined here, in the same order
   * as they are expected in the actual constructor. Note that the parameters
   * can value literals, property placeholders or references to other beans.
   * If this method is not invoked, the container will use the default
   * (parameterless) constructor to create an instance of this bean.
   *
   * @param args
   *       arguments that are used when invoking the constructor of this
   *       bean
   * @return the instance of this bean
   */
  def usingConstructorArgs(args: Any*): Bean = {
    this.args = args.toList
    this
  }

  /**
   * Use this method to inject dependencies through setter methods. First Scala
   * and then Java setters are taken into the account when scanning the class
   * for viable methods.
   * An example:
   *
   * <pre>
   * class User {
   *
   *   var username: String
   *   private var password: String
   *
   *   def setPassword(password: String) {
   *     this.password = password
   *   }
   * }
   *
   * new Bean named 'user
   * implementedWith classOf[User]
   * inject("admin" -> 'username,
   *        "adminadmin" -> 'password)
   * </pre>
   *
   * Value literals, property placeholders and other dependencies are all viable
   * injectables and will be resolved by the container when creating an
   * instance of this bean.
   *
   * @param pairs
   *       pairs of setter dependency definitions, first item is the value
   *       that will be injected while the second item is the actual
   *       property of the class that will be injected through a setter
   *       method
   * @return the instance of this bean
   */
  def inject(pairs: (Any, Symbol)*): Bean = {
    injectables = pairs.toList ::: injectables
    this
  }

  /**
   * Invoke this with a method that you wish this bean to be initalized
   * with. Method with the given name will be invoked on the bean after an
   * instance has been created and all necessary dependencies have been
   * injected.
   *
   * @param initMethod
   *         name of the method this bean should be initialized with
   * @param args
   *       optional arguments that should be supplied to the initialization
   *       method. They have to be in the same order as the method expects
   *       them
   * @return the instance of this bean
   */
  def initializeWith(initMethod: Symbol, args: Any*): Bean = {
    this.initMethod = initMethod
    initArgs = args.toList
    this
  }

  /**
   * Sets the scope of this bean as <tt>singleton</tt>, meaning that only one
   * instance of this bean will be created by the container and will be shared
   * among other beans that depend on it.
   * Scope singleton is default scope and invoking this method is purely
   * optional, use it only if you want to emphasize the scope of the declared
   * bean to other readers of your code.
   *
   * @return the instance of this bean
   */
  def scopedAsSingleton: Bean = {
    this.scope = Scope.Singleton
    this
  }

  /**
   * Sets the scope of this bean as <tt>prototype</tt>, meaning that a new
   * instance of this bean is created whenever this bean should injected as a
   * dependency or an instance has been explicitly requested from the container.
   * Also, <tt>prototype</tt> scope implies that this bean will not be eagerly
   * instantiated.
   *
   * @return the instance of this bean
   */
  def scopedAsPrototype: Bean = {
    this.scope = Scope.Prototype
    this
  }

  /**
   * If this method is invoked on a bean, it will not be eagerly instantiated
   * during the container's loading phase. It will only be created when it is
   * required by another bean or it has been requested from the container.
   * Use this only on the beans that are expensive to create resource-wise,
   * otherwise eager instantiation allows you to catch any possible errors in
   * initalization earlier.
   * Note that this method has no effect if invoked on a bean that has bean
   * scoped as prototype.
   *
   * @return the instance of this bean
   */
  def loadLazily: Bean = {
    this.lazyBean = true
    this
  }

  override def toString(): String = {
    var items = new mutable.ListBuffer[String]()
    if (beanNamed) items += "name = " + name.name
            else items += "Unnamed bean"
    if (clazz != null) items += "class = " + clazz
    items += "scope = " + scope
    items += "lazy = " + lazyBean

    items.mkString("Bean [", ", ", "]")
  }

}

