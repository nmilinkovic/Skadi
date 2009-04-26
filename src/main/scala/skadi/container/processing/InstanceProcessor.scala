package skadi.container.processing

import skadi.beans.FactoryBean

trait InstanceProcessor {

  def process(bean: FactoryBean): FactoryBean
}
