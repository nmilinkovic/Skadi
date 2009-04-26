package skadi.util

import java.util.logging.Logger

/**
 * Provides a default logger to the implementing class.
 * 
 * @author Nikola Milinkovic
 */
private[skadi] trait Loggable {
  
  protected val log = Logger.getLogger(getClass.getName)
  
}
