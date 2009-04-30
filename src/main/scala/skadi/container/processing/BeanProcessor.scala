package skadi.container.processing

import skadi.beans.Bean

/**
 * Performs processing on a sequence of user defined beans.
 *
 * @author Nikola Milinkovic
 */
private[container] trait BeanProcessor extends Processor[Seq[Bean]]
