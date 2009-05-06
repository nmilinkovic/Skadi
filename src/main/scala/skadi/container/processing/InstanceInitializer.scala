package skadi.container.processing

import skadi.beans.Bean
import skadi.util.ReflectionUtils

/**
 * Invokes init method on the bean if it has one declared.
 */
private[container] class InstanceInitializer extends InstanceProcessor {

  override def process(bean: Bean): Bean = {

    if (bean.initMethod != null) {

      val methodName = bean.initMethod.name
      log.info("Initializing " + bean + " with init method '" + methodName + "'." )
      val argVals = bean.initArgs.toArray
      val argTypes = ReflectionUtils.getTypes(argVals)
      val method = ReflectionUtils.findMethod(methodName, argTypes, bean.clazz)
      val argRefs = ReflectionUtils.anyToRef(argVals)
      assume(method.isDefined)

      method.get.invoke(bean.instance, argRefs: _*)
    }

    bean
  }

}
