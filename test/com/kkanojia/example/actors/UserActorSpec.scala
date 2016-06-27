package com.kkanojia.example.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.kkanojia.example.actors.UserActor._
import com.kkanojia.example.models.User
import com.kkanojia.example.utils.exceptions.UserPresentException
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.ReplicationEndpoint._
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class UserActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {


  def this() = this(ActorSystem("UserActorSpec"))

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(logId = "UserActorSpec"))(_system)
  val eventLog = endpoint.logs(DefaultLogName)


  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  "A User Actor" must {

    "be able to persist create user to event log" in {
      //Arrange
      val user = User("email@domain")
      val actorRef = system.actorOf(Props(new UserActor(user.id, Some(user.id), eventLog)))

      //Act
      actorRef ! CreateUser(user)

      //Assert
      expectMsgPF() {
        case UserCreationSuccess(createdUser) =>
          createdUser mustBe user
        case _ => fail
      }
    }

    "return failure message when asked to create duplicate user" in {
      //Arrange
      val user = User("email@domain")
      val actorRef = system.actorOf(Props(new UserActor(user.id, Some(user.id), eventLog)))
      actorRef ! CreateUser(user); expectMsgType[UserCreationSuccess]

      //Act
      actorRef ! CreateUser(user)

      //Assert
      expectMsgPF() {
        case UserCreationFailed(cause) =>
          cause mustBe an[UserPresentException.type]
        case _ => fail
      }
    }

    "return user when asked with GetUser command" in {
      //Arrange
      val user = User("email@domain")
      val actorRef = system.actorOf(Props(new UserActor(user.id, Some(user.id), eventLog)))
      actorRef ! CreateUser(user); expectMsgType[UserCreationSuccess]

      //Act
      actorRef ! GetUser

      //Assert
      expectMsgPF() {
        case UserRetrievalSuccess(retrievedUser) =>
          retrievedUser mustBe user
        case _ => fail
      }
    }

  }


}
