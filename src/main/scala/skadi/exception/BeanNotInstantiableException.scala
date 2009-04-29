package skadi.exception

/**
 * Thrown when a bean that could not be instantiated is requested from the
 * container.
 *
 * @author Nikola Milinkovic
 */
class BeanNotInstantiableException(msg: String) extends SkadiException(msg)
