package skadi.container.processing

trait Reaper {

  def destroy(instance: Any): Boolean

}
