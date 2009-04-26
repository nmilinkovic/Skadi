package com.sample.app

import skadi.beans.{Bean,Beans,BeansDefinition,Val}


object AppBeansDefinition extends BeansDefinition {

  override def getBeans: Seq[Bean] = {
    Beans(
      new Bean named 'postDao
      implementedWith classOf[com.sample.app.dao.PostDaoImpl],

      new Bean named 'userDao
      implementedWith classOf[com.sample.app.dao.UserDaoImpl]
      usingConstructorArgs 'userDao
      loadLazily,

      new Bean named 'userService
      implementedWith classOf[com.sample.app.service.UserServiceImpl]
      usingConstructorArgs('userDao, 'postDao, 5)
      inject(100 -> 'maxPosts),

      new Bean named 'user
      implementedWith classOf[com.sample.app.model.User]
      usingConstructorArgs("username", "pass")
      scopedAsPrototype,

      new Bean named 'admin
      implementedWith classOf[com.sample.app.model.User]
      usingConstructorArgs(Val("admin"), Val("admin"))
      scopedAsPrototype
    )
  }

}
