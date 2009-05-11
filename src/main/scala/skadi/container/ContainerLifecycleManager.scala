package skadi.container

import scala.collection.mutable

import skadi.beans.Bean
import skadi.container.processing.BeanProcessor
import skadi.container.validation.Validator
import skadi.util.Loggable

/**
 * Defines the algorithm and components that are required to initalize and
 * finalize the application container instance.
 *
 * @author Nikola Milinkovic
 */
private[container] trait ContainerLifecycleManager extends Loggable {

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
   * Define processors that that will operate on validated beans before the
   * context is created.
   */
  protected def preprocessors: List[BeanProcessor]

  /**
   * Define processors that will operate on beans after the context has been
   * created.
   */
  protected def postprocessors: List[BeanProcessor]

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
        log.severe(msg)
      } else {
        log.info("Validation complete.")

        // process the beans
        log.info("Starting processing...")
        var preprocessedBeans = preprocessors.foldLeft(beans)(processBeans(_, _))
        log.info("Processing complete.")

        // populate the context
        context ++= preprocessedBeans.map(b => (b.name, b))
        log.info("Context created with " + context.size + " beans.")

        // post-process the beans in the context
        postprocessors.foldLeft(preprocessedBeans)(processBeans(_, _))
        log.info("Container started.")

      }
    } catch {
      case e: Throwable => log.severe(e.getMessage)
    }
  }

  private def processBeans(beans: Seq[Bean], processor: BeanProcessor): Seq[Bean] = {
    processor.process(beans)
  }

}
