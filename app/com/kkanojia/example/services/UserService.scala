package com.kkanojia.example.services

import javax.inject.Inject
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.kkanojia.example.actors.{UserActor, UserManager}
import UserActor.{CreateUser, UserCreationFailed, UserCreationSuccess, UserRetrievalSuccess}
import com.kkanojia.example.models.User
import UserManager.RetrieveUser

import play.api.Logger

class UserService @Inject()(system: ActorSystem)(implicit ec: ExecutionContext) {

  implicit val timeout = Timeout(10 seconds)

  val userManager = Await.result(system.actorSelection("user/" + UserManager.NAME).resolveOne(), timeout.duration)

  def createUser(email: String): Future[Option[User]] = {
    val user = User(email)
    userManager ? CreateUser(user) map {
      case UserCreationSuccess(createdUser) =>
        Some(createdUser)
      case UserCreationFailed(cause) =>
        Logger.error(s"Error occurred while creating user ${cause.getMessage}")
        None
    }
  }

  def retrieveUser(email: String): Future[Option[User]] = {
    userManager ? RetrieveUser(email) map {
      case UserRetrievalSuccess(user) => Some(user)
      case _ => None
    }
  }
}
