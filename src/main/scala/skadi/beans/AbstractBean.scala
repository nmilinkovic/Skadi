package skadi.beans

private[skadi] abstract class AbstractBean {

  var name = Symbol(java.util.UUID.randomUUID.toString)
  var clazz: Class[_] = null
  var args: List[Any] = Nil
  var injectables: List[(Any, Symbol)] = Nil
  var initMethod: Symbol = null
  var initArgs: List[Any] = Nil
  var scope = Scope.Singleton
  var lazyBean = false
  var beanNamed = false

}
