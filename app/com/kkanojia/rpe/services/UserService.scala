package com.kkanojia.rpe.services

import javax.inject.Inject
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.kkanojia.rpe.actors.UserActor.{CreateUser, UserCreationFailed, UserCreationSuccess, UserRetrievalSuccess}
import com.kkanojia.rpe.actors.UserManager
import com.kkanojia.rpe.actors.UserManager.RetrieveUser
import com.kkanojia.rpe.models.User

class UserService @Inject()(system: ActorSystem)(implicit ec: ExecutionContext) {

  implicit val timeout = Timeout(15 seconds)

  val userManager = Await.result(system.actorSelection("user/" + UserManager.NAME).resolveOne(), timeout.duration)

  def createUser(email: String): Future[Option[User]] = {
    val user = User(email)
    userManager ? CreateUser(user) map {
      case UserCreationSuccess(createdUser) =>
        Some(createdUser)
      case UserCreationFailed(cause) =>
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
