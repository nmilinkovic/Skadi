package skadi.container

import org.junit.Assert._
import org.junit.Test

import com.sample.app.model._
import com.sample.app.dao._
import com.sample.app.service._

import skadi.exception.BeanNotFoundException;

class BeanRepositoryTest {

  // class under test
  private val repository: BeanRepository = new Container(com.sample.app.AppBeansDefinition.getBeans)

  @Test
  def testGetBean {

    val userDao = repository.getBean[UserDao]('userDao)
    assertNotNull(userDao)

    // check that we got same reference for the singleton
    val anotherReference = repository.getBean[UserDao]('userDao)
    assertSame(userDao, anotherReference)

    val user = repository.getBean[User]('user)
    val anotherUser = repository.getBean[User]('user)
    assertNotSame(user, anotherUser)

    var exceptionCaught = false
    try {
      repository.getBean[UserDao]('nosuchbean)
    } catch {
      case e: BeanNotFoundException => exceptionCaught = true
    }
    assertTrue(exceptionCaught)

    exceptionCaught = false
    try {
      repository.getBean[UserDao]('user)
    } catch {
      case e: ClassCastException => exceptionCaught = true
    }
    assertTrue(exceptionCaught)
  }

  @Test
  def testGetOptionalBean {
    val userDao = repository.getOptionalBean[UserDao]('userDao)
    assertTrue(userDao.isDefined)

    val undefinedBean = repository.getOptionalBean[UserDao]('nosuchbean)
    assertTrue(undefinedBean.isEmpty)

    val nonCastableBean = repository.getOptionalBean[UserDao]('user)
    assertTrue(nonCastableBean.isEmpty)
  }


  @Test
  def testContainsBean {
    assertTrue(repository.containsBean('userDao))
    assertFalse(repository.containsBean('noSuchBean))
  }

  @Test
  def testGetBeansCount {
    assertEquals(5, repository.getBeansCount)
  }

  @Test
  def testGetAllBeanNames {
    val names = repository.getAllBeanNames
    assertEquals(5, names.size)
    assertTrue(names.contains('userDao))
    assertFalse(names.contains('noSuchBean))
  }

  @Test
  def testGetBeanNamesForExactType {
    val names = repository.getBeanNamesForExactType[com.sample.app.model.User]
    assertEquals(2, names.size)
    assertTrue(names.contains('user))
    assertTrue(names.contains('admin))
    assertFalse(names.contains('nosuchuser))
  }

  @Test
  def testGetAssignableBeanNamesForType {
    val names = repository.getAssignableBeanNamesForType[com.sample.app.dao.UserDaoImpl]
    assertEquals(1, names.size)
    assertTrue(names.contains('userDao))
    assertFalse(names.contains('postDao))
  }

}
