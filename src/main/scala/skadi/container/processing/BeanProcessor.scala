package skadi.container.processing

import skadi.beans.Bean

trait BeanProcessor {

  def process(beans: Seq[Bean]): Seq[Bean]

}
