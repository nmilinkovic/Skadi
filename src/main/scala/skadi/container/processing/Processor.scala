package skadi.container.processing

import skadi.util.Loggable

private[processing] trait Processor[T] extends Loggable {

  def process(entity: T): T

}
