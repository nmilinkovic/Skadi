package skadi.container

import skadi.beans.Bean;
import skadi.container.processing._

class Container(beans: Seq[Bean]) extends BeanRepository {

  override protected def validators = Nil

  override protected def preprocessors = new TopologicalBeanSorter ::
                                         new PropertiesResolver :: Nil

  override protected def postprocessors = new EagerLoader(this) :: Nil

  override protected def instanceProcessors = new InstanceInitializer :: Nil

  initialize(beans)

}
