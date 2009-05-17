package skadi.container

import skadi.beans.Bean
import skadi.container.processing._
import skadi.util.Loggable

class Container(beans: Seq[Bean])
                extends BeanRepository
                   with BeanEvaluator
                   with InstanceFactory
                   with ContainerLifecycleManager
                   with ContextHolder
                   with Loggable {

  override protected val validators = Nil

  override protected val preprocessors = new TopologicalBeanSorter ::
                                         new PropertiesResolver :: Nil

  override protected val postprocessors = new EagerLoader(this) :: Nil

  override protected val instanceProcessors = new InstanceInitializer :: Nil

  initialize(beans)

}
