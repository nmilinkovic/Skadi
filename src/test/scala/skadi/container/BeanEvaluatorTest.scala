package skadi.container

import org.junit.{Assert, Test}
import com.sample.app.dao._
import com.sample.app.service._
import com.sample.app.model._

import skadi.util.BeanUtils

class BeanEvaluatorTest {

  private val undefinedName = 'nosuchbean
  private val beans = BeanUtils.createFactoryBeans(com.sample.app.AppBeansDefinition.getBeans)

  // class under test
  private val evaluator = new BeanRepositoryImpl
  evaluator.init(beans)

  @Test
  def testIsSingleton = {
    checkThrowsException(undefinedName, evaluator.isSingleton)
    Assert.assertTrue(evaluator.isSingleton('postDao))
    Assert.assertFalse(evaluator.isSingleton('user))
  }

  @Test
  def testIsPrototype = {
    checkThrowsException(undefinedName, evaluator.isPrototype)
    Assert.assertFalse(evaluator.isPrototype('postDao))
    Assert.assertTrue(evaluator.isPrototype('user))
  }

  @Test
  def testIsAssignable = {
    var exceptionCaught = false
    try {
      evaluator.isAssignable(undefinedName, classOf[String])
    } catch {
      case e: skadi.exception.BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)

    Assert.assertTrue(evaluator.isAssignable('userDao, classOf[UserDao]))
    Assert.assertFalse(evaluator.isAssignable('postDao, classOf[UserDao]))
  }

  @Test
  def testIsLazy = {
    checkThrowsException(undefinedName, evaluator.isLazy)
    Assert.assertTrue(evaluator.isLazy('userDao))
    Assert.assertFalse(evaluator.isLazy('user))
  }

  @Test
  def testGetType = {
    checkThrowsException(undefinedName, evaluator.getType)
    Assert.assertEquals(classOf[UserDaoImpl], evaluator.getType('userDao))
    Assert.assertNotSame(classOf[UserDaoImpl], evaluator.getType('postDao))
  }

  @Test
  def testGetOptionalType = {
    val undefined = evaluator.getOptionalType(undefinedName)
    Assert.assertFalse(undefined.isDefined)
    val defined = evaluator.getOptionalType('userDao)
    Assert.assertTrue(defined.isDefined)
  }

  private def checkThrowsException(beanName: Symbol, f:(Symbol) => Any) = {
    var exceptionCaught = false
    try {
      f(beanName)
    } catch {
      case e: skadi.exception.BeanNotFoundException => exceptionCaught = true
    }
    Assert.assertTrue(exceptionCaught)
  }

}
