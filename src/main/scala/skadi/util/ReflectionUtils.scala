package skadi.util

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Utility class that provides convenience methods for dealing with reflection
 * APIs.
 *
 * @author Nikola Milinkovic
 */
private[skadi] final object ReflectionUtils {

  private val scalaSuffix = "_$eq"
  private val javaPrefix = "set"

  /**
   * Attempts to find a constructor in the given class that matches the supplied
   * argument types. Will take into the account the overloaded constructors that
   * have both the primitive and Object version of the parameters.
   *
   * @param clazz
   *           class to be searched for a suitable constructor
   * @param argTypes
   *           agument types that the constructor should match
   * @return Some constructor if it is found, None if there is no matching
   *          constructor
   * @throws IllegalArgumentException if either passed clazz or argTypes are null
   */
  def findConstructor(clazz: Class[_], argTypes: Array[Class[_]]): Option[Constructor[_]] = {

    require(clazz != null)
    require(argTypes != null)
    val candidates =  for {
      candidate <- clazz.getConstructors
      if matches(candidate, argTypes)
    } yield candidate

    if (candidates.isEmpty) None
    else Some(candidates.first)
  }

  /**
   * Attempts to find a setter method in the given class that is used to set
   * the argument with the given argument name and type. It will first attempt
   * to find the Scala convention setter (<code>xxx_=</code>) and failing
   * that it will try to find a Java convention setter (<code>setXxx</code>). If
   * neither is found, <code>None</code> is returned.
   *
   * @param fieldName
   *               name of the field whose setter we are attempting to find
   * @param argType
   *               type of the argument that the setter method expects
   * @param clazz
   *               class that we are inspecting to find the method
   * @return <code>Some</code> method if it was found, <code>None</code> otherwise
   *
   * @throws IllegalArgumentException if any of the supplied arguments is null
   */
  def findSetter(fieldName: Symbol, argType: Class[_], clazz: Class[_]): Option[Method] = {

    require(fieldName != null)
    require(argType != null)
    require(clazz != null)

    val scalaName = fieldName.name + scalaSuffix
    val scalaMethod = getMethod(scalaName, argType, clazz)
    if (scalaMethod.isDefined) scalaMethod
    else {
      val javaName = javaPrefix + fieldName.name.capitalize
      getMethod(javaName, argType, clazz)
    }
  }

  /**
   * Convenience method that converts an array of Any objects to an array of
   * AnyRefs. This is useful when instantiating a new instance of an object
   * with reflection (when calling newInstance, because otherwise scala compiler
   * gets confused attempting to apply implicit conversions).
   *
   * @param args
   *           an array of Any to be converted
   * @return an array of references
   *
   * @throws IllegalArgumentException if null is passed to the method
   */
  def anyToRef(args: Array[Any]): Array[AnyRef] = {
    require(args != null)
    args.map(anyToRef(_))
  }

  /**
   * Determines the correct class of a primitive value.
   *
   * @param x
   *         some primitive value
   * @return the type of the given primitive value
   *
   * @throws IllegalArgumentException if x's type is not supported
   */
  def getType(x: Any): Class[_] = x match {
    case _: Byte => java.lang.Byte.TYPE
    case _: Short => java.lang.Short.TYPE
    case _: Int => java.lang.Integer.TYPE
    case _: Long => java.lang.Long.TYPE
    case _: Float => java.lang.Float.TYPE
    case _: Double => java.lang.Double.TYPE
    case _: Char => java.lang.Character.TYPE
    case _: Boolean => java.lang.Boolean.TYPE
    case _: Unit => java.lang.Void.TYPE
    case x: AnyRef => x.getClass
    case _ => error(x + " is not of supported type.")
  }

  /**
   * Converts <code>Any</code> to <code>AnyRef</code>. This is needed when
   * invoking constructors or methods that expect <code>java.lang.Object</code>
   * parameters.
   *
   * @param x
   *         an object of type <code>Any</code>
   * @return the supplied object converted to <code>AnyRef</code>
   */
  def anyToRef(x: Any): AnyRef = x match {
    case r: AnyRef => r
    case v: AnyVal => valToRef(v)
  }

  private def getMethod(methodName: String, argType: Class[_], clazz: Class[_]): Option[Method] = {
    try {
      val method = clazz.getMethod(methodName, argType)
      Some(method)
    } catch {
      case e: java.lang.NoSuchMethodException => None
    }
  }

  private def matches(constructor: Constructor[_], argTypes: Array[Class[_]]): Boolean = {

    val paramTypes = constructor.getParameterTypes
    if (argTypes.length == paramTypes.length) {
      val pairs = paramTypes zip argTypes
      pairs.forall(x => x._1 isAssignableFrom x._2)
    } else false
  }

  private def valToRef(x: AnyVal): AnyRef = x match {
    case x: Byte => Byte.box(x)
    case x: Short => Short.box(x)
    case x: Int => Int.box(x)
    case x: Long => Long.box(x)
    case x: Float => Float.box(x)
    case x: Double => Double.box(x)
    case x: Char => Char.box(x)
    case x: Boolean => Boolean.box(x)
    case x: Unit => ()
  }

}
