package com.kkanojia.example.actors

import scala.util.{Failure, Success}

import akka.actor.{ActorRef, Props}
import com.kkanojia.example.models.User
import com.kkanojia.example.utils.exceptions.UserPresentException
import com.rbmhtechnology.eventuate.EventsourcedActor

object UserActor {

  //Command
  case class CreateUser(user: User)
  case object GetUser

  //Response
  case class UserCreationSuccess(user: User)
  case class UserCreationFailed(cause: Throwable)
  case class UserRetrievalSuccess(user: User)
  case object UserRetrievalFailure

  //Event
  case class UserCreated(user: User)

}

/**
 * Event sourced actor to persist user events to event log
 *
 */
class UserActor(override val id: String,
                override val aggregateId: Option[String],
                override val eventLog: ActorRef,
                val managerId: Option[String] = None)
  extends EventsourcedActor {

  import UserActor._

  private var userOpt: Option[User] = None

  override def onCommand: Receive = {

    case CreateUser(user) if userOpt.isDefined =>
      sender() ! UserCreationFailed(UserPresentException)

    case CreateUser(user) =>
      persist(UserCreated(user), Set(managerId.getOrElse(""))) {
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
      val name = s"tm_${user.id}"
      context.actorOf(Props(new TradeManager(user.id.toString, Some(user.id.toString), eventLog)), name)
  }

}
