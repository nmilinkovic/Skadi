package skadi.exception

/**
 * This exception is thrown when a bean with the requested name is not found in
 * the application context.
 *
 * @author Nikola Milinkovic
 */
class BeanNotFoundException(msg: String) extends SkadiException(msg)
