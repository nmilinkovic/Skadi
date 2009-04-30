package skadi.container

import skadi.beans.Bean;
import skadi.container.processing.TopologicalBeanSorter
import skadi.container.processing.PropertiesResolver

class Container(beans: Seq[Bean]) extends BeanRepository {

  override protected def validators = Nil

  override protected def beanProcessors = new TopologicalBeanSorter ::
                                          new PropertiesResolver :: Nil

  override protected def instanceProcessors = Nil

  initialize(beans)

}
