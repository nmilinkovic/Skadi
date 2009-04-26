package skadi.util

import org.junit.{Assert, Test}

class BeanUtilsTest {

  @Test
  def testGetArgType {
    println("TODO implement test")
  }
  
  @Test
  def testGetArgTypes {
    println("TODO implement test")
  }
  
  @Test
  def testGetSetterArgTypes {
    println("TODO implement test")
  }
  
  @Test
  def testCreateFactoryBeansBeans {
    val beans = com.sample.app.AppBeansDefinition.getBeans.toList
    val factoryBeans = BeanUtils.createFactoryBeans(beans).toList
    val pairs = beans zip factoryBeans

    Assert.assertEquals(beans.size, factoryBeans.size)
    Assert.assertTrue(factoryBeans.forall(_.constructor != null))
    Assert.assertTrue(pairs.forall(x => x._1.name == x._2.name))
  }

}
