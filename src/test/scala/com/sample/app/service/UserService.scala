package com.sample.app.service

import com.sample.app.dao.{PostDao, UserDao}

trait UserService {

  def maxPosts: Int
  
  def authenticate(username: String, password: String): Boolean

  def getMaxUsers: Int

}

class UserServiceImpl(userDao: UserDao, postDao: PostDao, var maxUsers: Int)
  extends UserService {

  var maxPosts = 0  
    
  override def authenticate(username: String, password: String): Boolean = true

  override def getMaxUsers = maxUsers
  
  def setMaxUsers(maxUsers: Int) = this.maxUsers = maxUsers
  
}
