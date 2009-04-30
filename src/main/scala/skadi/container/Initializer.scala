package skadi.container

import scala.collection.mutable

import skadi.beans.Bean
import skadi.container.processing.BeanProcessor
import skadi.container.validation.Validator
import skadi.util.Loggable

/**
 * Defines the algorithm and components that are required to initalize the
 * application container instance.
 *
 * @author Nikola Milinkovic
 */
private[container] trait Initializer extends Loggable {

  /**
   * The application context, contains each defined bean registered under its
   * unique name in the map.
   */
  protected val context = new mutable.HashMap[Symbol, Bean]

  /**
   * Define the validators that should perform the validation on the supplied
   * user defined beans.
   */
  protected def validators: List[Validator]

  /**
   * Define processors that will process the validated bean.
   */
  protected def beanProcessors: List[BeanProcessor]

  /**
   * Implement this method to perform the initalization of a single bean within
   * the context.
   */
  protected def initInstance(bean: Bean): Bean

  /**
   * Initializes the context by processing the supplied beans.
   */
  protected def initialize(beans: Seq[Bean]): Unit = {

    try {

      // validate user input
      log.info("Starting validation...")
      val errors = validators.foldRight(List[String]())(_.validate(beans) ::: _)
      if (!errors.isEmpty) {
        val msg = errors.mkString("Unable to initialize the container. Reason:\n", "\n", "\nExiting...")
        exitOnError(msg)
      }
      log.info("Validation complete.")

      // process the beans
      log.info("Starting processing...")
      var processedBeans = beanProcessors.foldRight(beans)(_.process(_))
      log.info("Processing complete.")

      // populate the context
      context ++= processedBeans.map(b => (b.name, b))
      log.info("Context created with " + context.size + " beans.")


      processedBeans.foreach(initInstance(_))
      log.info("Container started.")

    } catch {
      case e: Throwable => exitOnError(e.getMessage)
    }
  }

  private[this] def exitOnError(message:String) {
    log.severe(message)
    //System.exit(1)
  }

}
