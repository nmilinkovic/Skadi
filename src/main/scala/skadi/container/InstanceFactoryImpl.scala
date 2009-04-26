package skadi.container

import java.lang.reflect.Modifier

import skadi.beans.Val
import skadi.beans.FactoryBean;
import skadi.beans.Scope
import skadi.exception.BeanNotFoundException;
import skadi.util.BeanUtils
import skadi.util.ReflectionUtils

private[container] class InstanceFactoryImpl extends InstanceFactory {

  private var beansMap: Map[Symbol, FactoryBean] = null

  protected def context = beansMap

  override protected def instanceProcessors = Nil

  private[container] def init(beans: Seq[FactoryBean]): Unit = {
    beansMap = BeanUtils.createFactoryBeansMap(beans)
    beans.foreach(initBean(_))
  }

  override def getBean[T](name: Symbol): T = {
    val bean = findBean(name)
    if (bean.instance == null || bean.scope == Scope.Prototype)
      createInstance(bean).asInstanceOf[T]
    else
      bean.instance.asInstanceOf[T]
  }

  override def getOptionalBean[T](name: Symbol): Option[T] = {
    val bean = beansMap.get(name)
    if (bean.isDefined) Some(getBean[T](name))
    else None
  }

  override def createBeanWithConstructorArgs[T](name: Symbol, args: Any*): T = {
    val bean = findBean(name)
    createInstance(bean, args.toArray, bean.injectables).asInstanceOf[T]
  }

  override def createBeanWithInjectables[T](name: Symbol,
                                            injectables: (Any, Symbol)*): T = {
    val bean = findBean(name)
    createInstance(bean, bean.args.toArray, injectables).asInstanceOf[T]
  }

  override def createBean[T](name: Symbol, args: Seq[Any],
                             injectables: Seq[(Any, Symbol)]): T = {
    val bean = findBean(name)
    createInstance(bean, args.toArray, injectables).asInstanceOf[T]
  }

  protected def findBean(name: Symbol): FactoryBean = {
    val bean = beansMap.get(name)
    if (bean.isEmpty) {
      throw new BeanNotFoundException("Bean named '" + name.name + """' is not
                                       registered in the application context.""")
    }
    bean.get
  }

  private def initBean(bean: FactoryBean): FactoryBean = {
    if (!(Modifier.isAbstract(bean.clazz.getModifiers) || bean.lazyBean
        || bean.scope == Scope.Prototype)) {
      bean.instance = createInstance(bean)
    }
    bean
  }

  private def createInstance(bean: FactoryBean): Any = {
    createInstance(bean, bean.args.toArray, bean.injectables)
  }

  private def createInstance(bean: FactoryBean, args: Array[Any],
                             injectables: Iterable[(Any, Symbol)]): Any = {

    val instance = if (args.isEmpty) {
      bean.constructor.newInstance()
    } else {
      val argVals = args.map(getArgValue(_))
      val argRefs = ReflectionUtils.anyToRef(argVals)
      bean.constructor.newInstance(argRefs: _*)
    }

    injectWithSetterDependencies(instance, bean.clazz, injectables)
  }

  private def injectWithSetterDependencies(instance: Any, clazz: Class[_],
                                           injectables: Iterable[(Any, Symbol)]): Any = {

    injectables.foreach(injectSetterDependency(instance, clazz, _))
    instance
  }

  private def injectSetterDependency(instance: Any, clazz: Class[_],
                                           injectable: (Any, Symbol)): Any = {

    val argType = BeanUtils.getSetterArgType(injectable, beansMap)
    val argValue = getArgValue(injectable._1)
    val argRef = ReflectionUtils.anyToRef(argValue)
    val fieldName = injectable._2
    val setter = ReflectionUtils.findSetter(fieldName, argType, clazz)
    assume(setter.isDefined)
    setter.get.invoke(instance, argRef)
    instance
  }

  private def getArgValue(arg: Any): Any = arg match {
    case s: Symbol if (beansMap.contains(s)) => getBean(s)
    case Val(v) => v
    case _ => arg
  }

}
