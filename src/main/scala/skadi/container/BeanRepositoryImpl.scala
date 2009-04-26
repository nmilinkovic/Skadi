package skadi.container

import java.lang.reflect.Modifier

import scala.reflect.Manifest

import skadi.beans.FactoryBean;
import skadi.beans.Scope
import skadi.exception.BeanNotFoundException;

private[container] class BeanRepositoryImpl extends InstanceFactoryImpl
  with BeanRepository with BeanEvaluator {

  override def containsBean(name: Symbol): Boolean = context.contains(name)

  override def getAllBeanNames(): Set[Symbol] = Set.empty ++ context.keySet

  override def getBeanNamesForExactType[T](implicit m: Manifest[T]): Set[Symbol] = {
    val names = for {
      bean <- context.values
      if (bean.clazz == m.erasure)
    } yield bean.name
    Set.empty ++ names
  }

  override def getAssignableBeanNamesForType[T](implicit m: Manifest[T]):
    Set[Symbol] = {
    val names = for {
      bean <- context.values
      if (isAssignable(bean.name, m.erasure))
    } yield bean.name
    Set.empty ++ names
  }

  override def isSingleton(name: Symbol): Boolean = {
    val bean = findBean(name)
    bean.scope == Scope.Singleton
  }

  override def isPrototype(name: Symbol): Boolean = {
    val bean = findBean(name)
    bean.scope == Scope.Prototype
  }

  override def isAssignable(name: Symbol, clazz: Class[_]): Boolean = {
    val bean = findBean(name)
    clazz.isAssignableFrom(bean.clazz)
  }

  override def isLazy(name: Symbol): Boolean = {
    val bean = findBean(name)
    bean.lazyBean
  }

  override def getType(name: Symbol): Class[_] = {
    val bean = findBean(name)
    bean.clazz
  }

  override def getOptionalType(name: Symbol): Option[Class[_]] = {
    val bean = context.getOrElse(name, null)
    if (bean == null) None
    else Some(bean.clazz)
  }

}
