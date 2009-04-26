package skadi.container.processing

import skadi.beans.FactoryBean

/**
 * Performs processing on a sequence of the factory beans that were created by
 * the container.
 *
 * @author Nikola Milinkovic
 */
private[container] trait FactoryBeanProcessor {

  /**
   * Process the supplied sequence of beans, and return the result. When
   * chaining multiple processors together, make sure that the next processor in
   * chain uses the result of the processing instead of the original sequence
   * (in case that a new instance was created).
   *
   * @param beans
   *       a sequence of factory beans to be processed
   * @return a (possibly new) sequence of processed beans
   */
  def process(beans: Seq[FactoryBean]): Seq[FactoryBean]

}
