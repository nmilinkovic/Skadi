package skadi.util

import skadi.beans.Val
import skadi.beans.AbstractBean
import skadi.beans.Bean;
import skadi.beans.FactoryBean;

/**
 * Utility class that provides convenience methods for dealing wiht both user
 * defined and factory beans.
 *
 * @author Nikola Milinkovic
 */
private[skadi] object BeanUtils {

  /**
   * Determines the actual type (class) of the given argument.
   *
   * @param arg
   *           argument whose type is being determined
   * @beansMap
   *           context in which the given argument is being evaluated
   * @throws RuntimeException
   *           in case that the supplied argument type is not supported
   */
  def getArgType(arg: Any, beansMap: Map[Symbol, AbstractBean]): Class[_] = arg match {
    case s: Symbol if (beansMap.contains(s)) => beansMap.get(s).get.clazz
    case Val(v) => ReflectionUtils.getType(v)
    case v: AnyVal => ReflectionUtils.getType(v)
    case r: AnyRef => r.getClass
    case _ => error(arg + " is not of supported type.")
  }

  /**
   * Determines the actual types (classes) of the given constructor arguments.
   *
   * @param args
   *           arguments whose types are being determined
   * @beansMap
   *           context in which the given arguments are being evaluated
   * @throws RuntimeException
   *           in case that a supplied argument's type is not supported
   * @throws IllegalArgumentException if the supplied args or map are null
   */
  def getArgTypes(args: Array[Any], beansMap: Map[Symbol, AbstractBean]): Array[Class[_]] = {
      require(args != null)
      require(beansMap != null)
      args.map(getArgType(_, beansMap))
  }

  /**
   * Determines the actual type (class) of the given setter argument.
   *
   * @param setterArg
   *           argument whose type is being determined
   * @beansMap
   *           context in which the given argument is being evaluated
   * @throws RuntimeException
   *           in case that a supplied argument's type is not supported
   * @throws IllegalArgumentException if the supplied arg or map are null
   */
  def getSetterArgType(setterArg: (Any, Symbol), beansMap: Map[Symbol, AbstractBean]): Class[_] = {
    require(setterArg != null)
    require(beansMap != null)
    getArgType(setterArg._1, beansMap)
  }

  /**
   * Creates factory beans from the user defined beans. Determines the proper
   * constructor based on the argument types.
   *
   * @param beans user defined beans that will be used to create factory beans
   *
   * @return newly created factory beans
   *
   * @throws IllegalArgumentException if the passed beans are null
   */
  def createFactoryBeans(beans: Seq[Bean]): Seq[FactoryBean] = {
    require(beans != null)
    val beansMap = createBeansMap(beans)
    beans.map(createFactoryBean(_, beansMap))
  }

  /**
   * Constructs a map from the given beans by using the bean name as a key and
   * the bean instance as value.
   *
   * @param beans
   *             beans to be used to construct the map
   * @return the newly constructed map
   *
   * @throws IllegalArgumentException if the passed beans are null
   */
  def createBeansMap(beans: Seq[Bean]): Map[Symbol, Bean] = {
    require(beans != null)
    Map.empty ++ beans.map(b => (b.name, b))
  }

  /**
   * Constructs a map from the given factory beans by using the bean name as a
   * key and the bean instance as value.
   *
   * @param beans
   *             beans to be used to construct the map
   * @return the newly constructed map
   *
   * @throws IllegalArgumentException if the passed beans are null
   */
  def createFactoryBeansMap(beans: Seq[FactoryBean]): Map[Symbol, FactoryBean] = {
    require(beans != null)
    Map.empty ++ beans.map(b => (b.name, b))
  }

  /**
   * Extracts the names of the given beans and returns them as a sequence.
   *
   * @param beans
   *             beans whose names will be extracted
   * @return a sequence of bean names
   *
   * @throws IllegalArgumentException if the passed beans are null
   */
  def extractBeanNames(beans: Seq[Bean]): Seq[Symbol] = {
    require(beans != null)
    beans.map(_.name)
  }

  private def createFactoryBean(bean: Bean, beansMap: Map[Symbol, Bean]): FactoryBean = {

    val argTypes = getArgTypes(bean.args.toArray, beansMap)
    val constructor = ReflectionUtils.findConstructor(bean.clazz, argTypes)
    assume(constructor.isDefined)
    val factoryBean = new FactoryBean(bean)
    factoryBean.constructor = constructor.get

    factoryBean
  }


}
