package skadi.container

import org.junit.{Assert, Test}
import com.sample.app.dao._
import com.sample.app.service._

import skadi.exception.BeanNotFoundException;

class InstanceFactoryTest {

  // class under test
  private val factory: InstanceFactory = new Container(com.sample.app.AppBeansDefinition.getBeans)



  @Test
  def testCreateBeanWithConstructorArgs {
    val maxUsers = 3
    val userService = factory.createBeanWithConstructorArgs[UserService]('userService, 'userDao, 'postDao, maxUsers)
    Assert.assertEquals(maxUsers, userService.getMaxUsers)

    var exceptionCaught = false
    try {
      factory.createBeanWithConstructorArgs[UserService]('nosuchbean, 'userDao, 'postDao, maxUsers)
    } catch {
      case e: BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)
  }

  @Test
  def testCreateBeanWithInjectables {
    val maxPosts = 50
    val maxUsers = 5
    val userService = factory.createBeanWithInjectables[UserService]('userService,
                                                                     maxPosts -> 'maxPosts,
                                                                     maxUsers -> 'maxUsers)
    Assert.assertNotNull(userService)
    Assert.assertEquals(maxPosts, userService.maxPosts)
    Assert.assertEquals(maxUsers, userService.getMaxUsers)

    var exceptionCaught = false
    try {
      factory.createBeanWithInjectables[UserService]('nosuchbean,
                                                     maxPosts -> 'maxPosts,
                                                     maxUsers -> 'maxUsers)
    } catch {
      case e: BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)
  }

  @Test
  def testCreateBean {
    val maxPosts = 50
    val maxUsers = 5
    val args = Seq('userDao, 'postDao, maxUsers)
    val injectables = Array(maxPosts -> 'maxPosts, maxUsers -> 'maxUsers)
    val userService = factory.createBean[UserService]('userService, args, injectables)
    Assert.assertNotNull(userService)
    Assert.assertEquals(maxPosts, userService.maxPosts)
    Assert.assertEquals(maxUsers, userService.getMaxUsers)

    var exceptionCaught = false
    try {
      factory.createBean[UserService]('nosuchbean, args, injectables)
    } catch {
      case e: BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)
  }

}
