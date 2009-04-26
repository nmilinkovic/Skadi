package skadi.container.processing


import skadi.beans.{Bean, Beans, Prop}

class User(val username: String, val password: String)

object Main extends Application{

    // class under test
  val resolver = new PropertiesResolver
  testProcess

  def testProcess {
    val beans = Beans(

      new Bean named 'admin
      implementedWith classOf[User]
      usingConstructorArgs("${user.name}", "${admin.pass}"),

      new Bean implementedWith classOf[skadi.util.PropertiesHandle]
      inject("app.properties" -> 'files)
    )

    val newbeans = resolver.process(beans)
    newbeans.foreach(println)

  }


}
