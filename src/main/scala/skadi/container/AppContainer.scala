package skadi.container

import skadi.beans.Bean
import skadi.container.processing.PropertiesResolver
import skadi.container.processing.TopologicalBeanSorter
import skadi.container.validation.Validator

class AppContainer(beans: Seq[Bean]) extends BeanRepositoryImpl with Container {

  override protected def validator = new Validator {
    override def validate(beans: Seq[Bean]) = Nil
  }

  override protected def preprocessors = new TopologicalBeanSorter ::
                                         new PropertiesResolver :: Nil

  override protected def postprocessors = Nil

  load(beans)
}
