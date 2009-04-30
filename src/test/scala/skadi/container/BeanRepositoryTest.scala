package skadi.container

import org.junit.{Assert, Test}
import com.sample.app.dao._
import com.sample.app.service._

import skadi.exception.BeanNotFoundException;

class BeanRepositoryTest {

  // class under test
  private val repository: BeanRepository = new Container(com.sample.app.AppBeansDefinition.getBeans)

  @Test
  def testGetBean {
    val userDao = repository.getBean[UserDao]('userDao)
    Assert.assertNotNull(userDao)

    var exceptionCaught = false
    try {
      repository.getBean[UserDao]('nosuchbean)
    } catch {
      case e: BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)
  }

  @Test
  def testGetOptionalBean {
    val userDao = repository.getOptionalBean[UserDao]('userDao)
    Assert.assertTrue(userDao.isDefined)

    val undefinedBean = repository.getOptionalBean[UserDao]('nosuchbean)
    Assert.assertTrue(undefinedBean.isEmpty)
  }


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
