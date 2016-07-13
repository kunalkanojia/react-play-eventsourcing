package com.kkanojia.example.models

import java.util.UUID

case class User(
  id: String,
  email: String
)

object User {

  def apply(email: String): User = {
    User(UUID.randomUUID().toString, email)
  }
}