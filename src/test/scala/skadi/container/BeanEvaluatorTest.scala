package skadi.container

import org.junit.{Assert, Test}
import com.sample.app.dao._
import com.sample.app.service._
import com.sample.app.model._

import skadi.util.BeanUtils
import skadi.exception.BeanNotFoundException

class BeanEvaluatorTest {

  private val undefinedName = 'nosuchbean

  // class under test
  private val evaluator: BeanEvaluator = new Container(com.sample.app.AppBeansDefinition.getBeans)


  @Test { val expected = classOf[BeanNotFoundException] }
  def testIsSingleton = {

    Assert.assertTrue(evaluator.isSingleton('postDao))
    Assert.assertFalse(evaluator.isSingleton('user))

    evaluator.isSingleton(undefinedName)
    ()
  }

  @Test { val expected = classOf[BeanNotFoundException] }
  def testIsPrototype = {

    Assert.assertFalse(evaluator.isPrototype('postDao))
    Assert.assertTrue(evaluator.isPrototype('user))

    evaluator.isPrototype(undefinedName)
    ()
  }

  @Test { val expected = classOf[BeanNotFoundException] }
  def testIsAssignable = {

    Assert.assertTrue(evaluator.isAssignable('userDao, classOf[UserDao]))
    Assert.assertFalse(evaluator.isAssignable('postDao, classOf[UserDao]))

    evaluator.isAssignable(undefinedName, classOf[String])
    ()
  }

  @Test { val expected = classOf[BeanNotFoundException] }
  def testIsLazy = {

    Assert.assertTrue(evaluator.isLazy('userDao))
    Assert.assertFalse(evaluator.isLazy('user))

    evaluator.isLazy(undefinedName)
    ()
  }

  @Test { val expected = classOf[BeanNotFoundException] }
  def testGetType = {

    Assert.assertEquals(classOf[UserDaoImpl], evaluator.getType('userDao))
    Assert.assertNotSame(classOf[UserDaoImpl], evaluator.getType('postDao))

    evaluator.getType(undefinedName)
    ()
  }

  @Test
  def testGetOptionalType = {

    val undefined = evaluator.getOptionalType(undefinedName)
    Assert.assertFalse(undefined.isDefined)

    val defined = evaluator.getOptionalType('userDao)
    Assert.assertTrue(defined.isDefined)
  }

}
