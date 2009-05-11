package skadi.container.processing

import org.junit.Assert._
import org.junit.Test

import skadi.beans.Bean
import skadi.container.Container

class EagerLoaderTest {

  @Test
  def testProcess {

    val beans = List(
      new Bean named 'abstract
      implementedWith classOf[AbstractBean],

      new Bean named 'singleton
      implementedWith classOf[SingletonBean]
      scopedAsSingleton,

      new Bean named 'prototype
      implementedWith classOf[PrototypeBean]
      scopedAsPrototype,

      new Bean named 'eager
      implementedWith classOf[EagerBean],

      new Bean named 'lazy
      implementedWith classOf[LazyBean]
      loadLazily

    )

    val container = new Container(beans)

    assertFalse(Context.abstractLoaded)
    assertTrue(Context.singletonLoaded)
    assertFalse(Context.prototypeLoaded)
    assertTrue(Context.eagerLoaded)
    assertFalse(Context.lazyLoaded)

  }

}

object Context {

  var abstractLoaded = false

  var singletonLoaded = false

  var prototypeLoaded = false

  var eagerLoaded = false

  var lazyLoaded = false

}

abstract class AbstractBean {
  Context.abstractLoaded = true
}

class SingletonBean {
  Context.singletonLoaded = true
}


class PrototypeBean {
  Context.prototypeLoaded = true
}

class EagerBean {
  Context.eagerLoaded = true
}

class LazyBean {
  Context.lazyLoaded = true
}
