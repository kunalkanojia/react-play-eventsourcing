package com.kkanojia.example.actors

import scala.collection._

import akka.actor.{ActorRef, Props}
import com.kkanojia.example.utils.exceptions.UserPresentException
import com.rbmhtechnology.eventuate.EventsourcedView

object UserManager{

  //Command
  case class RetrieveUser(email: String)

  val ID = "um_id_dd782f9d"
  val NAME = "user_manager"
}

class UserManager(override val id: String,
                  override val aggregateId: Option[String],
                  override val eventLog: ActorRef) extends EventsourcedView {

  import UserActor._
  import UserManager._

  private val usersInSystem = mutable.Map[String, String]()

  override def onCommand: Receive = {

    case CreateUser(user) =>
      if(usersInSystem.contains(user.email))
        sender() ! UserCreationFailed(UserPresentException)
      else
      getUserActor(user.id.toString) forward CreateUser(user)

    case RetrieveUser(email: String) =>
      usersInSystem.get(email) match {
        case Some(userId) => getUserActor(userId.toString) forward GetUser
        case None => sender() ! UserRetrievalFailure
      }

  }

  override def onEvent: Receive = {
    case UserCreated(user) => usersInSystem(user.email) = user.id
  }

  private def getUserActor(userId: String) : ActorRef =  {
    val name = s"user_$userId"
    context.child(name) match {
      case Some(actorRef) => actorRef
      case None => context.actorOf(Props(new UserActor(userId, Some(userId), eventLog, Some(id))), name)
    }
  }

}
