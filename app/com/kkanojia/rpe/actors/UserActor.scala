package com.kkanojia.rpe.actors

import scala.util.{Failure, Success}

import akka.actor.{ActorRef, Props}
import com.kkanojia.rpe.actors.UserActor._
import com.kkanojia.rpe.models.User
import com.kkanojia.rpe.utils.exceptions.UserPresentException
import com.rbmhtechnology.eventuate.EventsourcedActor

object UserActor {

  //Command
  case class CreateUser(user: User)
  case object GetUser

  //Response
  case class UserCreationFailed(cause: Throwable)

  case class UserCreationSuccess(user: User)
  case object UserRetrievalFailure
  case class UserRetrievalSuccess(user: User)

  //Event
  case class UserCreated(user: User)

}

class UserActor(override val id: String,
                override val aggregateId: Option[String],
                override val eventLog: ActorRef)
  extends EventsourcedActor {

  private var userOpt: Option[User] = None

  override def onCommand: Receive = {

    case CreateUser(user) if userOpt.isDefined =>
      sender() ! UserCreationFailed(UserPresentException)

    case CreateUser(user) =>
      persist(UserCreated(user), Set(UserManager.ID)) {
        case Success(event) =>
          sender() ! UserCreationSuccess(user)
        case Failure(error) =>
          UserCreationFailed(error)
      }

    case GetUser =>
      userOpt match {
        case Some(user) => sender() ! UserRetrievalSuccess(user)
        case None => sender() ! UserRetrievalFailure
      }
  }

  override def onEvent: Receive = {

    case UserCreated(user) =>
      userOpt = Some(user)
      val name = s"tm_${user.id.toString}"
      context.actorOf(Props(new TradeManager(user.id.toString, Some(user.id.toString), eventLog)), name)
  }

}
