package skadi.container.processing

import org.junit.{Assert, Test}

import skadi.beans.{Bean, Beans, Prop}

class PropertiesResolverTest {

  // class under test
  val resolver = new PropertiesResolver

  @Test
  def testProcess {
    val beans = Beans(
      new Bean named 'postDao
      implementedWith classOf[com.sample.app.dao.PostDaoImpl],

      new Bean named 'userDao
      implementedWith classOf[com.sample.app.dao.UserDaoImpl],

      new Bean named 'userService
      implementedWith classOf[com.sample.app.service.UserServiceImpl]
      usingConstructorArgs('userDao, 'postDao, Prop("max.users"))
      inject("${max.posts}" -> 'maxPosts),

      new Bean named 'admin
      implementedWith classOf[com.sample.app.model.User]
      usingConstructorArgs("${user.name}", "${admin.pass}"),

      new Bean implementedWith classOf[skadi.util.PropertiesHandle]
      inject("app.properties;props.xml" -> 'files)
    )

    val processedBeans = resolver.process(beans)
    val userServiceBean = processedBeans(2)
    Assert.assertEquals("100", userServiceBean.injectables.first._1)
    Assert.assertEquals("5", userServiceBean.args(2))

    val adminBean = processedBeans(3)
    val username = System.getProperty("user.name")
    Assert.assertEquals(username, adminBean.args(0))
    Assert.assertEquals("adminadmin", adminBean.args(1))
  }
}
