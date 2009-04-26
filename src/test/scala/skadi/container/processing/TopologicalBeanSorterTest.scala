package skadi.container.processing

import org.junit.{Assert, Test}

import skadi.beans.{Bean, Beans}

class TopologicalBeanSorterTest {

  //class under test
  val sorter = new TopologicalBeanSorter

  @Test
  def testSort {
    val beans = Beans(

      new Bean named 'postService
      usingConstructorArgs('postDao),

      new Bean named 'userService
      inject('postDao -> 'postDao,
             'userDao -> 'userDao),

      new Bean named 'blogServlet
      usingConstructorArgs('userService, 'postService),

      new Bean named 'userDao,

      new Bean named 'postDao
    )

    val sortedBeans = sorter.process(beans)
    Assert.assertFalse(sortedBeans.isEmpty)
    Assert.assertEquals(5, sortedBeans.size)
    Assert.assertTrue('blogServlet == sortedBeans(4).name)
  }

}
