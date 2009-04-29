package skadi.container

import java.lang.reflect.Modifier

import skadi.beans.Bean
import skadi.beans.FactoryBean
import skadi.container.processing.BeanProcessor
import skadi.container.processing.FactoryBeanProcessor
import skadi.container.validation.Validator
import skadi.util.BeanUtils
import skadi.util.Loggable

/**
 * Defines the algorithm and components that are required to initalize the
 * application container instance.
 *
 * @author Nikola Milinkovic
 */
private[container] trait Initializer extends Loggable {

  private var beansMap: Map[Symbol, FactoryBean] = null

  /**
   * The application context, contains each defined bean registered under its
   * unique name in the map.
   */
  protected def context = beansMap

  /**
   * Define the validators that should perform the validation on the supplied
   * user defined beans.
   */
  protected def validators: List[Validator]

  /**
   * Define preprocessors that will process the validated bean.
   */
  protected def preprocessors: List[BeanProcessor]

  /**
   * Define the processors that will operate on the created factory beans.
   */
  protected def postprocessors: List[FactoryBeanProcessor]

  /**
   * Override this method to perform the initalization of a single bean within
   * the context.
   */
  protected def initBean(bean: FactoryBean): FactoryBean

  /**
   * Initializes the context by processing the supplied beans.
   */
  protected def initialize(beans: Seq[Bean]): Unit = {

    try {

      // validate
//      val errorMessages = validator.validate(beans)
//      if (!errorMessages.isEmpty) {
//        val msg = errorMessages.mkString("Unable to load context. Reason:\n", "\n", "\nExiting...")
//        exitOnError(msg)
//      }

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

     beansMap = BeanUtils.createFactoryBeansMap(factoryBeans)
     factoryBeans.foreach(initBean(_))

    } catch {
      case e: Throwable => exitOnError(e.getMessage)
    }
  }

  private[this] def exitOnError(message:String) {
    log.severe(message)
    System.exit(1)
  }

}
