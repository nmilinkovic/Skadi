package skadi.container

import scala.collection.mutable

import skadi.beans.Bean

private[skadi] trait ContextHolder {

  /**
   * The application context, contains each defined bean registered under its
   * unique name in the map.
   */
  protected val context = new mutable.HashMap[Symbol, Bean]

}
