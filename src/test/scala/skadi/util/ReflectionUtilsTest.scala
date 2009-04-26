package skadi.util

import org.junit.{Assert, Test}

class ReflectionUtilsTest {
  
  @Test
  def testAnyToRef {
    val array = List[Any](true, 2, "a string").toArray
    val refsArray = ReflectionUtils.anyToRef(array)
    Assert.assertEquals(array.length, refsArray.length)    
  }
  
  @Test
  def testGetType {
    Assert.assertEquals(java.lang.Boolean.TYPE, ReflectionUtils.getType(true))
    Assert.assertEquals(java.lang.Integer.TYPE, ReflectionUtils.getType(1))
  }
  
  @Test
  def testFindConstructor {
    val args1: Array[Class[_]] = Array(classOf[Int], classOf[String])
    val result11 = ReflectionUtils.findConstructor(classOf[Class1], args1)
    Assert.assertTrue(result11.isDefined)
    
    val args2: Array[Class[_]] = Array(classOf[Class1], classOf[Boolean], 
      classOf[String])
    val result21 = ReflectionUtils.findConstructor(classOf[Class2], args1)    
    val result22 =  ReflectionUtils.findConstructor(classOf[Class2], args2)
    Assert.assertTrue(result21.isEmpty)
    Assert.assertTrue(result22.isDefined)
    
    val args3: Array[Class[_]] = Array(classOf[Class2])
    val result31 = ReflectionUtils.findConstructor(classOf[Class3], args1)
    val result32 = ReflectionUtils.findConstructor(classOf[Class3], args2)
    val result33 = ReflectionUtils.findConstructor(classOf[Class3], args3)
    Assert.assertTrue(result31.isEmpty)
    Assert.assertTrue(result32.isEmpty)
    Assert.assertTrue(result33.isDefined)
    
    val args4: Array[Class[_]] = Array()
    val result41 = ReflectionUtils.findConstructor(classOf[Class4], args1)
    val result42 = ReflectionUtils.findConstructor(classOf[Class4], args2)
    val result43 = ReflectionUtils.findConstructor(classOf[Class4], args3)
    val result44 = ReflectionUtils.findConstructor(classOf[Class4], args4)
    Assert.assertTrue(result41.isEmpty)
    Assert.assertTrue(result42.isEmpty)
    Assert.assertTrue(result43.isEmpty)
    Assert.assertTrue(result44.isDefined)
  }
  
  @Test
  def testFindSetter {
    val scalaMethod = ReflectionUtils.findSetter('scalaVariable, classOf[String],
                                                 classOf[ClassWithSetters])
    Assert.assertTrue(scalaMethod.isDefined)
    
    val javaMethod = ReflectionUtils.findSetter('javaVariable, classOf[String], 
                                                classOf[ClassWithSetters])
    Assert.assertTrue(javaMethod.isDefined)
    
    val noSuchMethod = ReflectionUtils.findSetter('noSuchVariable, classOf[String], 
                                                classOf[ClassWithSetters])
    Assert.assertTrue(noSuchMethod.isEmpty)
  }  
}

trait Trait1

class Class1(arg1: Int, arg2: String) extends Trait1

class Class2(arg1: Trait1, arg2: Boolean, arg3: String)

class Class3(arg1: Class2)

class Class4

class ClassWithSetters {
  var scalaVariable = "scalaVariable"
  private var javaVariable = "javaVariable"
  def setJavaVariable(arg: String) = javaVariable = arg  
}
