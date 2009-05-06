package skadi.beans

import org.hamcrest.CoreMatchers._
import org.junit.Assert.assertThat
import org.junit.Test

class PropertyTest {

  @Test
  def testProperty {
    assertThat(isProperty("${jdbc.url}"), is(true))
    assertThat(isProperty(" ${jdbc.url} "), is(true))
    assertThat(isProperty("%{jdbc.url}"), is(false))
    assertThat(isProperty("this is not a property"), is(false))
  }

  private def isProperty(x: Any): Boolean = x match {
      case Property(value) => true
      case _ => false
  }

}





































