package skadi.beans

private[skadi] sealed abstract class Dependency
//case class Ref(value: Symbol) extends Dependency
case class Val(value: Any) extends Dependency
case class Prop(value: String) extends Dependency
