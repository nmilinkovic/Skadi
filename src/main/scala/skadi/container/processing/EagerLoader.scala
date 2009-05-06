package skadi.container.processing

import skadi.beans.Bean
import skadi.beans.Scope
import skadi.container.InstanceFactory
import skadi.util.ReflectionUtils

/**
 * Creates instances for all concrete, non-lazy, singleton beans.
 *
 * @author Nikola Milinkovic
 */
private[container] class EagerLoader(factory: InstanceFactory) extends BeanProcessor {

  override def process(beans: Seq[Bean]): Seq[Bean] = {
    log.info("Eagerly loading beans...")
    beans.map(initialize(_))
  }

  private def initialize(bean: Bean): Bean = {
    val concrete = ReflectionUtils.isConcrete(bean.clazz)
    val eager = !bean.lazyBean
    val singleton = bean.scope == Scope.Singleton
    if (concrete && eager && singleton) {
      log.info("Creating instance of " + bean)
      bean.instance = factory.createInstance(bean)
    }
    bean
  }

}
