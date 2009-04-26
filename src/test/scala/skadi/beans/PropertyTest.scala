package skadi.beans

import org.junit.{Assert, Test}

class PropertyTest {

  @Test
  def testProperty {
    Assert.assertTrue(isProperty("${jdbc.url}"))
    Assert.assertTrue(isProperty(" ${jdbc.url} "))
    Assert.assertFalse(isProperty("%{jdbc.url}"))
    Assert.assertFalse(isProperty("this is not a property"))
  }

  private def isProperty(x: Any): Boolean = x match {
      case Property(value) => true
      case _ => false
  }

}





































