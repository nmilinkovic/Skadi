package skadi.container.processing

import skadi.beans.Bean

/**
 * Performs processing on a sequence of user defined beans.
 *
 * @author Nikola Milinkovic
 */
private[container] trait BeanProcessor {

  /**
   * Process the supplied sequence of beans, and return the result. When
   * chaining multiple processors together, make sure that the next processor in
   * chain uses the result of the processing instead of the original sequence
   * (in case that a new instance was created).
   *
   * @param beans
   *       a sequence of user defined beans to be processed
   * @return a (possibly new) sequence of processed beans
   */
  def process(beans: Seq[Bean]): Seq[Bean]

}
