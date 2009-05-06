package skadi.container

import java.lang.reflect.Constructor

import skadi.beans.Bean
import skadi.beans.Scope
import skadi.beans.Val
import skadi.container.processing.InstanceProcessor
import skadi.exception.BeanNotFoundException
import skadi.exception.BeanNotInstantiableException
import skadi.util.ReflectionUtils._

/**
 * Defines methods for the on-demand creation of user defined beans.
 *
 * @author Nikola Milinkovic
 */
trait InstanceFactory extends ContainerLifecycleManager {

  /**
   * A list of processors that will be invoked on every instance that is
   * created by this factory.
   */
  protected def instanceProcessors: List[InstanceProcessor]

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
   * Finds the bean with the given name in the context. Throws an exception if the
   * bean is not there.
   *
   * @param name
   *   name of the bean to be looked up
   * @return the requested factory bean
   *
   * @throws BeanNotFoundException if the bean is not found
   */
  protected def findBean(name: Symbol): Bean = {
    val bean = context.get(name)
    if (bean.isEmpty) {
      throw new BeanNotFoundException("Bean named '" + name.name + "' is not registered in the container!")
    }
    bean.get
  }

  /**
   * Returns an instance of the specified bean. It will create a new instance
   * if the bean is not instantiated yet or if it is scoped as prototype.
   */
  protected def getInstance(bean: Bean): Any = {

    if (isAbstract(bean.clazz)) {
      throw new BeanNotInstantiableException("Bean '" + bean.name +"' is abstract and therefore"
                           + " cannot be instantiated!")
    }

    if (bean.instance == null || bean.scope == Scope.Prototype)
      createInstance(bean)
    else
      bean.instance
  }

  private def findMatchingConstructor(bean: Bean, args: Array[Any]): Constructor[_] = {
    val argTypes = getArgTypes(args)
    val constructor = findConstructor(bean.clazz, argTypes)
    if (constructor.isEmpty) {
      throw new NoSuchMethodException("Constructor with arguments (" + args.mkString(",")
                                      + ") is not defined for " + bean)
    }
    constructor.get
  }

  private[container] def createInstance(bean: Bean): Any = {
    if (bean.constructor == null) {
      bean.constructor = findMatchingConstructor(bean, bean.args.toArray)
    }
    createInstance(bean, bean.constructor, bean.args.toArray, bean.injectables)
  }

  private def createInstance(bean: Bean, constructor: Constructor[_], args: Array[Any],
                             injectables: Iterable[(Any, Symbol)]): Any = {

    val instance = if (args.isEmpty) {
      constructor.newInstance()
    } else {
      val argVals = args.map(getArgValue(_))
      val argRefs = anyToRef(argVals)
      constructor.newInstance(argRefs: _*)
    }
    injectWithSetterDependencies(instance, constructor.getDeclaringClass, injectables)
    // process the instance after it has been injected with dependencies
    instanceProcessors.foreach(_.process(bean))

    instance
  }

  private def injectWithSetterDependencies(instance: Any, clazz: Class[_], injectables: Iterable[(Any, Symbol)]): Any = {
    injectables.foreach(injectSetterDependency(instance, clazz, _))
    instance
  }

  private def injectSetterDependency(instance: Any, clazz: Class[_], injectable: (Any, Symbol)): Any = {

    val argType = getSetterArgType(injectable)
    val argValue = getArgValue(injectable._1)
    val argRef = anyToRef(argValue)
    val fieldName = injectable._2
    val setter = findSetter(fieldName, argType, clazz)
    if (setter.isEmpty) {
      throw new NoSuchMethodException("Setter for property '" + injectable._2.name
                                      + "' of type '" + argType.getName + "' is not defined in class "
                                      + clazz.getName + "!")
    }
    setter.get.invoke(instance, argRef)

    instance
  }

  private def getArgValue(arg: Any): Any = arg match {
    case s: Symbol if (context.contains(s)) => getInstance(context.get(s).get)
    case Val(v) => v
    case _ => arg
  }

  private def getArgType(arg: Any): Class[_] = arg match {
    case s: Symbol if (context.contains(s)) => context.get(s).get.clazz
    case Val(v) => getType(v)
    case v: AnyVal => getType(v)
    case r: AnyRef => r.getClass
    case _ => error(arg + " is not of supported type.")
  }

  private def getArgTypes(args: Array[Any]): Array[Class[_]] = args.map(getArgType(_))

  private def getSetterArgType(setterArg: (Any, Symbol)): Class[_] = getArgType(setterArg._1)

}
