package skadi.beans

/**
 * Use an instance of <tt>Dependency</tt> to explicitly declare the type of the
 * dependency (property placeholder or a value literal).
 *
 * @author Nikola Milinkovic
 */
private[skadi] sealed abstract class Dependency

/**
 * Use <tt>Val</tt> when you want Skadi to interpret your dependency as value
 * literal. This can be usefull when for example you want to inject a string
 * "${some.property}" in a bean and don't want it to be interpreted as a property
 * placeholder by the container.
 *
 * @author Nikola Milinkovic
 */
case class Val(value: Any) extends Dependency

/**
 * Use <tt>Prop</tt> when you want your dependecy to be interpeted as a property
 * placeholder, without using the placeholder notation (${...}).
 * Example:
 * <pre>
 *   new Bean named 'foo
 *   constructorArgs Prop("some.property")
 * </pre>
 *
 */
case class Prop(value: String) extends Dependency
