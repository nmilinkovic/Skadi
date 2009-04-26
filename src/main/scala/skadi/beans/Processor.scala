package skadi.beans

trait Processor[T] {

  def process(entity: T): T
  
}
