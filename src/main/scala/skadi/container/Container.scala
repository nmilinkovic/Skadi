package skadi.container

import skadi.beans.Bean;
import skadi.container.processing.BeanProcessor
import skadi.container.processing.FactoryBeanProcessor
import skadi.container.validation.Validator
import skadi.util.Loggable
import skadi.util.BeanUtils

trait Container extends BeanRepository with BeanEvaluator with InstanceFactory
  with Loggable {

  protected def validator: Validator
  protected def preprocessors: List[BeanProcessor]
  protected def postprocessors: List[FactoryBeanProcessor]

  protected def load(beans: Seq[Bean]): Unit = {
    try {

      // validate
      val errorMessages = validator.validate(beans)
      if (!errorMessages.isEmpty) {
        val msg = errorMessages.mkString("Unable to load context. Reason:\n",
                                         "\n", "\nExiting...")
        exitOnError(msg)
      }

      // pre-process
      var validatedBeans = beans
      for (preprocessor <- preprocessors){
        validatedBeans = preprocessor.process(validatedBeans)
      }

      // create factory beans
      var factoryBeans = BeanUtils.createFactoryBeans(validatedBeans)

      // post-process
      for (postprocessor <- postprocessors) {
        factoryBeans = postprocessor.process(factoryBeans)
      }
      init(factoryBeans)

    } catch {
      case e: Throwable => exitOnError(e.getMessage)
    }
  }

  private[this] def exitOnError(message:String) {
    log.severe(message)
    System.exit(1)
  }
}
