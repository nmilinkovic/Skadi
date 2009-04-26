package skadi.beans

object Beans {
  
  // should be able to accept collections of beans also (when getting beans from
  // multiple definition classes)
  def apply(beans: Bean*): List[Bean] = {
    beans.toList
  }
  
}
 