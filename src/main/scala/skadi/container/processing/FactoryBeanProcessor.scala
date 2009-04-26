package skadi.container.processing

import skadi.beans.FactoryBean

trait FactoryBeanProcessor {

  def process(beans: Seq[FactoryBean]): Seq[FactoryBean]

}
