package com.sample.app.dao

import com.sample.app.model.User

trait UserDao {
  
  def getUser(username: String): Option[User]
  
  def getAllUsers: Seq[User]
  
  def addUser(user:User): Boolean
  
}

class UserDaoImpl extends UserDao {
  
  var users: List[User] = Nil
  
  override def getUser(username: String): Option[User] = {
    users.filter(_.username == username).firstOption    
  }
  
  override def getAllUsers: Seq[User] = {
    users
  }
  
  override def addUser(user:User): Boolean = {
    if (users.filter(_.username == user.username).isEmpty) {
      users = user :: users      
      true
    } else false
  }

  
}
