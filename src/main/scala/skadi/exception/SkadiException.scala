package skadi.exception

/**
 * Base exception in the Skadi container. All container exceptions are derived
 * from this one.
 *
 * @author Nikola Milinkovic
 */
abstract class SkadiException(msg: String) extends RuntimeException(msg)
