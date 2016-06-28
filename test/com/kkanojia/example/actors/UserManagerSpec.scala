package com.kkanojia.example.actors

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.kkanojia.example.actors.UserActor._
import com.kkanojia.example.actors.UserManager.RetrieveUser
import com.kkanojia.example.models.User
import com.kkanojia.example.utils.exceptions.UserPresentException
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.ReplicationEndpoint._
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class UserManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {


  def this() = this(ActorSystem("UserManagerSpec"))

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(logId = "UserManagerSpec"))(_system)
  val eventLog = endpoint.logs(DefaultLogName)

  "A User Manager" must {

    "create a user and respond with create success" in {
      //Arrange
      val user = User("jerry@sienfeld")
      val id = UUID.randomUUID().toString
      val userManagerRef = system.actorOf(Props(new UserManager(id, Some(id), eventLog)))

      //Act
      userManagerRef ! CreateUser(user)

      //Assert
      expectMsgPF() {
        case UserCreationSuccess(createdUser) =>
          createdUser mustBe user
        case _ => fail
      }
    }

    "should not allow creation of user with duplicate email id" in {
      //Arrange
      val user = User("cosmo@kramer")
      val id = UUID.randomUUID().toString
      val userManagerRef = system.actorOf(Props(new UserManager(id, Some(id), eventLog)))
      userManagerRef ! CreateUser(user); expectMsgType[UserCreationSuccess]

      //Act
      userManagerRef ! CreateUser(user)

      //Assert
      expectMsgPF() {
        case UserCreationFailed(cause) =>
          cause mustBe an[UserPresentException.type]
        case _ => fail
      }
    }

    "return correct user when asked with email" in {
      //Arrange
      val user = User("elaine@benes")
      val id = UUID.randomUUID().toString
      val userManagerRef = system.actorOf(Props(new UserManager(id, Some(id), eventLog)))
      userManagerRef ! CreateUser(user); expectMsgType[UserCreationSuccess]

      //Act
      userManagerRef ! RetrieveUser(user.email)

      //Assert
      expectMsgPF() {
        case UserRetrievalSuccess(retrievedUser) =>
          retrievedUser mustBe user
        case _ => fail
      }
    }

    "respond with UserRetrievalFailure when user is not found in the system" in {
      //Arrange
      val user = User("art@vandelay")
      val id = UUID.randomUUID().toString
      val userManagerRef = system.actorOf(Props(new UserManager(id, Some(id), eventLog)))

      //Act
      userManagerRef ! RetrieveUser(user.email)

      //Assert
      expectMsgType[UserRetrievalFailure.type]
    }

  }

}
