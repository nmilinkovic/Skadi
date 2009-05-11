package skadi.container.processing

import org.junit.Assert._
import org.junit.Test

import skadi.beans.Bean
import skadi.container.Container

class InstanceInitializerTest {

  val beans = List(
    new Bean named 'noArgsInitializer
    implementedWith classOf[InitializableBean]
    initializeWith 'init,

    new Bean named 'argsInitializer
    implementedWith classOf[InitializableBean]
    initializeWith('init, 2),

    new Bean named 'uninitialized
    implementedWith classOf[InitializableBean]
  )

  @Test
  def testProcess {

    val container = new Container(beans)

    val noArgs = container.getBean[InitializableBean]('noArgsInitializer)
    assertEquals(1, noArgs.toBeInitialized)

    val args = container.getBean[InitializableBean]('argsInitializer)
    assertEquals(2, args.toBeInitialized)

    val uninit = container.getBean[InitializableBean]('uninitialized)
    assertEquals(0, uninit.toBeInitialized)
  }

}

class InitializableBean {

  var toBeInitialized = 0

  def init {
    toBeInitialized = 1
  }

  def init(i: Int) {
    toBeInitialized = i
  }

}
