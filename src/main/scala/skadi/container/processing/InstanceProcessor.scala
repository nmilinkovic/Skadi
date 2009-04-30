package skadi.container.processing

import skadi.beans.Bean

/**
 * Used to process each instance of a bean additionaly after it has been created.
 * Could be used for instance initialization, custom logging, etc.
 *
 * @author Nikola Milinkovic
 */
trait InstanceProcessor extends Processor[Bean]
