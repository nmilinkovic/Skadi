package skadi.util

/**
 * Define this bean when you want to add your property files to the container.
 * These properties will be then used to resolve any property placeholder
 * dependencies that you have defined in your beans.
 *
 * Example usage:
 * <pre>
 *   new Bean named 'baz
 *  implementedWith classOf[foo.bar.Baz]
 *   constructorArgs("${some.prop}", "${other.prop}")
 *
 *   new Bean
 *   implementedWith classOf[skadi.util.PropertiesHandle]
 *   inject "app.properties;properties.xml" -> 'files
 * </pre>
 *
 * @author Nikola Milinkovic
 */
class PropertiesHandle {

  /**
   * Semicolon delimited list of files that should be used when resolving
   * property placeholders. Both regular and xml property files are allowed.
   * Note that if the declared files are not found on the classpath, the
   * container will not start.
   */
  var files: String = null

}
