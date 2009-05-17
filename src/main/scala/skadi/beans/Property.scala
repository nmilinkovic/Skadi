package skadi.beans

/**
 * Extractor that is used to determine if the given argument is a property or
 * a different kind of dependency. Properties are formatted as
 * <code>${config.dir}</code> or used as a literal
 * <code>Prop("config.dir")</code> with a {@link skadi.Prop} case class.
 *
 * @author Nikola Milinkovic
 */
private[skadi] object Property {

  private val pattern = "\\A\\$\\{.+\\}\\Z" // example: ${sample.property}

  def unapply(value: String): Option[String] = value match {
    case prop: String if (value.trim.matches(pattern)) => Some(trimBraces(prop))
    case _ => None
  }

  private def trimBraces(property: String): String = {
    property.substring(property.indexOf("{") + 1, property.lastIndexOf("}"))
  }

}
