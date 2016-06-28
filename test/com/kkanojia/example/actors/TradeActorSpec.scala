package com.kkanojia.example.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.kkanojia.example.actors.TradeActor._
import com.kkanojia.example.models.Trade
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.ReplicationEndpoint._
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class TradeActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {


  def this() = this(ActorSystem("TradeActorSpec"))

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(logId = "TradeActorSpec"))(_system)
  val eventLog = endpoint.logs(DefaultLogName)

  "A trade actor" must {

    "be able to create a trade when called with `CreateTrade`" in {
      //Arrange
      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeActor = system.actorOf(Props(new TradeActor(trade.id, Some(trade.id), eventLog, "")))

      //Act
      tradeActor ! CreateTrade(trade)

      //Assert
      expectMsgPF(){
        case CreateTradeSuccess(createdTrade) =>
          createdTrade mustBe trade

        case CreateTradeFailure(cause) => fail
      }
    }

    "be able to update a trade when called with `UpdateTrade`" in {
      //Arrange
      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeActor = system.actorOf(Props(new TradeActor(trade.id, Some(trade.id), eventLog, "")))
      tradeActor ! CreateTrade(trade); expectMsgType[CreateTradeSuccess]

      //Act
      val updateTrade = trade.copy(assetId = 2)
      tradeActor ! UpdateTrade(updateTrade)

      //Assert
      expectMsgPF(){
        case UpdateTradeSuccess(updatedTrade) =>
          updatedTrade mustBe updateTrade

        case UpdateTradeFailure(cause) => fail
      }
    }

  }

}
