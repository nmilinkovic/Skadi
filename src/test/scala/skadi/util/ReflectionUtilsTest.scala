package skadi.util

import org.junit.{Assert, Test}

import skadi.util.ReflectionUtils._

class ReflectionUtilsTest {

  @Test
  def testAnyToRef {
    val array = List[Any](true, 2, "a string").toArray
    val refsArray = anyToRef(array)
    Assert.assertEquals(array.length, refsArray.length)
  }

  @Test
  def testGetType {
    Assert.assertEquals(java.lang.Boolean.TYPE, getType(true))
    Assert.assertEquals(java.lang.Integer.TYPE, getType(1))
  }

  @Test
  def testFindConstructor {
    val args1: Array[Class[_]] = Array(classOf[Int], classOf[String])
    val result11 = findConstructor(classOf[Class1], args1)
    Assert.assertTrue(result11.isDefined)

    val args2: Array[Class[_]] = Array(classOf[Class1], classOf[Boolean], classOf[String])
    val result21 = findConstructor(classOf[Class2], args1)
    val result22 =  findConstructor(classOf[Class2], args2)
    Assert.assertTrue(result21.isEmpty)
    Assert.assertTrue(result22.isDefined)

    val args3: Array[Class[_]] = Array(classOf[Class2])
    val result31 = findConstructor(classOf[Class3], args1)
    val result32 = findConstructor(classOf[Class3], args2)
    val result33 = findConstructor(classOf[Class3], args3)
    Assert.assertTrue(result31.isEmpty)
    Assert.assertTrue(result32.isEmpty)
    Assert.assertTrue(result33.isDefined)

    val args4: Array[Class[_]] = Array()
    val result41 = findConstructor(classOf[Class4], args1)
    val result42 = findConstructor(classOf[Class4], args2)
    val result43 = findConstructor(classOf[Class4], args3)
    val result44 = findConstructor(classOf[Class4], args4)
    Assert.assertTrue(result41.isEmpty)
    Assert.assertTrue(result42.isEmpty)
    Assert.assertTrue(result43.isEmpty)
    Assert.assertTrue(result44.isDefined)
  }

  @Test
  def testFindSetter {
    val scalaMethod = findSetter('scalaVariable, classOf[String], classOf[ClassWithSetters])
    Assert.assertTrue(scalaMethod.isDefined)

    val javaMethod = findSetter('javaVariable, classOf[String], classOf[ClassWithSetters])
    Assert.assertTrue(javaMethod.isDefined)

    val noSuchMethod = findSetter('noSuchVariable, classOf[String], classOf[ClassWithSetters])
    Assert.assertTrue(noSuchMethod.isEmpty)
  }

  @Test
  def testFindMethod {
    val noParamMethod = findMethod("init", Array[Class[_]](), classOf[Class5])
    Assert.assertTrue(noParamMethod.isDefined)

    val singleArgMethod = findMethod("init", Array(classOf[Int]), classOf[Class5])
    Assert.assertTrue(singleArgMethod.isDefined)

    val undefinedMethod = findMethod("nosuchmethod", Array(classOf[String]), classOf[Class5])
    Assert.assertFalse(undefinedMethod.isDefined)
  }

}

trait Trait1

class Class1(arg1: Int, arg2: String) extends Trait1

class Class2(arg1: Trait1, arg2: Boolean, arg3: String)

class Class3(arg1: Class2)

class Class4

class Class5 {

  def init = ()

  def init(p: Int) = ()

}

class ClassWithSetters {
  var scalaVariable = "scalaVariable"
  private var javaVariable = "javaVariable"
  def setJavaVariable(arg: String) = javaVariable = arg
}
