package skadi.container

import java.lang.reflect.Constructor
import java.lang.reflect.Modifier

import skadi.beans.FactoryBean;
import skadi.beans.Scope
import skadi.beans.Val
import skadi.container.processing.InstanceProcessor
import skadi.exception.BeanNotFoundException
import skadi.exception.BeanNotInstantiableException
import skadi.util.BeanUtils
import skadi.util.ReflectionUtils


/**
 * Defines methods for the on-demand retrieval and creation of the defined beans.
 *
 * @author Nikola Milinkovic
 */
trait InstanceFactory extends Initializer {

  /**
   * A list of processors that will be invoked on every instance that is
   * created by this factory.
   */
  protected def instanceProcessors: List[InstanceProcessor]

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
  def getBean[T](name: Symbol): T = {
    val bean = findBean(name)
    if (isAbstract(bean)) {
      throw new BeanNotInstantiableException("Bean '" + name.name +"' is abstract and therefore"
                           + " cannot be instantiated!")
    }
    if (bean.instance == null || bean.scope == Scope.Prototype)
      createInstance(bean).asInstanceOf[T]
    else
      bean.instance.asInstanceOf[T]
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
    val bean = context.get(name)
    if (bean.isDefined ) {
      Some(getBean[T](name))
    } else None
  }


  // should throw an exception if the bean is not there
  // unsupported yet
  def createBeanWithConstructorArgs[T](name: Symbol, args: Any*): T = {
    val bean = findBean(name)
    val constructor = findMatchingConstructor(bean, args.toArray)
    val instance = createInstance(bean, constructor, args.toArray, bean.injectables)
    instance.asInstanceOf[T]
  }

  // should throw an exception if the bean is not there
  // unsupported yet
  def createBeanWithInjectables[T](name: Symbol, injectables: (Any, Symbol)*): T = {
    val bean = findBean(name)
    val instance = createInstance(bean, bean.constructor, bean.args.toArray, injectables)
    instance.asInstanceOf[T]
  }

  //should throw an exception if the bean is not there
  // unsupported yet
  def createBean[T](name: Symbol, args: Seq[Any], injectables: Seq[(Any, Symbol)]): T = {
    val bean = findBean(name)
    val constructor = findMatchingConstructor(bean, args.toArray)
    val instance = createInstance(bean, constructor, args.toArray, injectables)
    instance.asInstanceOf[T]
  }

  /**
   * Initalize the bean on factory startup, but only if it is concrete, eagerly
   * loaded and scoped as singleton.
   *
   * @param bean factory bean that will be initialized
   *
   * @return factory bean with created instance, but only if it matches the
   * initialization terms
   */
  override protected def initBean(bean: FactoryBean): FactoryBean = {
    val concrete = !isAbstract(bean)
    val eager = !bean.lazyBean
    val singleton = bean.scope == Scope.Singleton
    if (concrete && eager && singleton) {
      bean.instance = createInstance(bean)
    }
    bean
  }

  /**
   * Retrieves the factory bean that is registered under the supplied name.
   *
   * @param name
   *       unique name under which the bean is registered
   * @return the requested factory bean
   *
   * @throws BeanNotFoundException
   */
  protected def findBean(name: Symbol): FactoryBean = {
    val bean = context.get(name)
    if (bean.isEmpty) {
      throw new BeanNotFoundException("Bean named '" + name.name + "' is not registered in the container!")
    }
    bean.get
  }

  private def isAbstract(bean: FactoryBean) = Modifier.isAbstract(bean.clazz.getModifiers)

  /**
   * Finds the constructor for the given bean that corresponds to the given
   * arguments array. If no matching constructor is found, an exception is thrown.
   *
   * @param bean
   *       bean whose constructor will be looked up
   * @param args
   *       arguments that should be supplied to the constructor
   * @return the constructor of that bean that can be used with the given arguments
   *
   * @throws NoSuchMethodException if no suitable constructor is found for the
   * given bean
   */
  private def findMatchingConstructor(bean: FactoryBean, args: Array[Any]): Constructor[_] = {
    val argTypes = BeanUtils.getArgTypes(args, context)
    val constructor = ReflectionUtils.findConstructor(bean.clazz, argTypes)
    if (constructor.isEmpty) {
      throw new NoSuchMethodException("Constructor with arguments (" + args.mkString(",")
                                      + ") is not defined for class " + bean.clazz.getName + "!")
    }
    constructor.get
  }

  private def createInstance(bean: FactoryBean): Any = {
    createInstance(bean, bean.constructor, bean.args.toArray, bean.injectables)
  }

  /**
   * Creates an instance of the class whose constructor was supplied with
   * the given arguments and afterwards injects the created instance with the
   * provided dependencies.
   *
   * @param bean
   *     bean definition
   * @param constructor
   *     constructor that will be used to create the instance
   * @param args
   *     constructor arguments that will be used when creating the instance
   * @param injectables
   *     dependencies that will be injected in the created instance
   * @return the newly created instance
   */
  private def createInstance(bean: FactoryBean, constructor: Constructor[_], args: Array[Any],
                             injectables: Iterable[(Any, Symbol)]): Any = {

    val instance = if (args.isEmpty) {
      constructor.newInstance()
    } else {
      val argVals = args.map(getArgValue(_))
      val argRefs = ReflectionUtils.anyToRef(argVals)
      constructor.newInstance(argRefs: _*)
    }
    injectWithSetterDependencies(instance, constructor.getDeclaringClass, injectables)
    instanceProcessors.foreach(_.process(bean))
    instance
  }

  private def injectWithSetterDependencies(instance: Any, clazz: Class[_], injectables: Iterable[(Any, Symbol)]): Any = {
    injectables.foreach(injectSetterDependency(instance, clazz, _))
    instance
  }

  /**
   * Injects a single dependency via a setter method to the instance of the
   * given class.
   *
   * @param instance
   *     instance that will be injected
   * @param clazz
   *     class of the given instance
   * @param injectable
   *     dependency to be injected
   * @return the instance that was injected
   *
   * @throws NoSuchMethodException if the supplied class has no required setter
   * method
   */
  private def injectSetterDependency(instance: Any, clazz: Class[_], injectable: (Any, Symbol)): Any = {

    val argType = BeanUtils.getSetterArgType(injectable, context)
    val argValue = getArgValue(injectable._1)
    val argRef = ReflectionUtils.anyToRef(argValue)
    val fieldName = injectable._2
    val setter = ReflectionUtils.findSetter(fieldName, argType, clazz)
    if (setter.isEmpty) {
      throw new NoSuchMethodException("Setter for property '" + injectable._2.name
                                      + "' of type '" + argType.getName + "' is not defined in class "
                                      + clazz.getName + "!")
    }
    setter.get.invoke(instance, argRef)

    instance
  }

  private def getArgValue(arg: Any): Any = arg match {
    case s: Symbol if (context.contains(s)) => getBean(s)
    case Val(v) => v
    case _ => arg
  }

}
