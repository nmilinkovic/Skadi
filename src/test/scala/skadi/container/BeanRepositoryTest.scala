package skadi.container

import org.junit.{Assert, Test}
import com.sample.app.dao._
import com.sample.app.service._

import skadi.util.BeanUtils

class BeanRepositoryTest {

  private val beans = BeanUtils.createFactoryBeans(com.sample.app.AppBeansDefinition.getBeans)

  // class under test
  private val repository= new BeanRepositoryImpl
  repository.init(beans)

  @Test
  def testContainsBean {
    Assert.assertTrue(repository.containsBean('userDao))
    Assert.assertFalse(repository.containsBean('noSuchBean))
  }

  @Test
  def testGetBeansCount {
    Assert.assertEquals(5, repository.getBeansCount)
  }

  @Test
  def testGetAllBeanNames {
    val names = repository.getAllBeanNames
    Assert.assertEquals(5, names.size)
    Assert.assertTrue(names.contains('userDao))
    Assert.assertFalse(names.contains('noSuchBean))
  }

  @Test
  def testGetBeanNamesForExactType {
    val names = repository.getBeanNamesForExactType[com.sample.app.model.User]
    Assert.assertEquals(2, names.size)
    Assert.assertTrue(names.contains('user))
    Assert.assertTrue(names.contains('admin))
    Assert.assertFalse(names.contains('nosuchuser))
  }

  @Test
  def testGetAssignableBeanNamesForType {
    val names = repository.getAssignableBeanNamesForType[com.sample.app.dao.UserDaoImpl]
    Assert.assertEquals(1, names.size)
    Assert.assertTrue(names.contains('userDao))
    Assert.assertFalse(names.contains('postDao))
  }

}
