package skadi.util

import org.junit.{Assert, Test}

import skadi.beans.Val

class BeanUtilsTest {

  val beans = com.sample.app.AppBeansDefinition.getBeans.toList
  val beansMap = BeanUtils.createBeansMap(beans)

  @Test
  def testGetArgType {
    val postDaoType = BeanUtils.getArgType('postDao, beansMap)
    Assert.assertEquals(classOf[com.sample.app.dao.PostDaoImpl], postDaoType)

    val intType = BeanUtils.getArgType(Val(5), beansMap)
    Assert.assertEquals(java.lang.Integer.TYPE, intType)

    val stringType = BeanUtils.getArgType("a string", beansMap)
    Assert.assertEquals(classOf[String], stringType)
  }

  @Test
  def testCreateFactoryBeansBeans {
    val factoryBeans = BeanUtils.createFactoryBeans(beans).toList
    val pairs = beans zip factoryBeans

    Assert.assertEquals(beans.size, factoryBeans.size)
    Assert.assertTrue(factoryBeans.forall(_.constructor != null))
    Assert.assertTrue(pairs.forall(x => x._1.name == x._2.name))
  }

}
