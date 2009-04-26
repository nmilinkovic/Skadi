package skadi.beans

/**
 * Implement this trait within a package to define the beans whose visibility
 * is set to package-private (only visible within that package). Example:
 * <pre>
 *   package foo.bar
 *
 *   private[bar] class Baz
 *
 *   object BarDefinition extends BeansDefinition {
 *
 *     override def getBeans: Seq[Bean] = {
 *       new Seq(
 *         new Bean named 'baz
 *         implementedWith classOf[foo.bar.Baz]
 *       )
 *     }
 *   }
 *
 * </pre>
 *
 * @author Nikola Milinkovic
 */
trait BeansDefinition {

  def getBeans: Seq[Bean]

}
