package com.sample.app.dao

import com.sample.app.model.{User, Post}

trait PostDao {
  
  def getPosts(user: User): List[Post]
  
  def addPost(user: User, post: Post): Post 
    
}

class PostDaoImpl extends PostDao {
  
  override def getPosts(user: User): List[Post] = {
    Nil
  } 
  
  override def addPost(user: User, post: Post): Post = {
    null
  }
  
}
