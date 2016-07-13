package com.kkanojia.example.actors

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.kkanojia.example.actors.TradeActor._
import com.kkanojia.example.actors.TradeManager.{FindTrade, FindTradeSuccess, RetrieveTrades, RetrieveTradesSuccess}
import com.kkanojia.example.models.Trade
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.ReplicationEndpoint._
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class TradeManagerSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers  {

  def this() = this(ActorSystem("TradeManagerSpec"))

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(logId = "TradeManagerSpec"))(_system)
  val eventLog = endpoint.logs(DefaultLogName)

  "A trade manager" must {

    "be able to create trade when called with `CreateTrade`" in {
      //Arrange
      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val id = UUID.randomUUID().toString
      val tradeManager = system.actorOf(Props(new TradeManager(id, Some(id), eventLog)))

      //Act
      tradeManager ! CreateTrade(trade)

      //Assert
      expectMsgPF(){
        case CreateTradeSuccess(createdTrade) =>
          createdTrade mustBe trade

        case CreateTradeFailure(cause) => fail
      }
    }

    "be able to update trade when called with `UpdateTrade`" in {
      //Arrange
      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val id = UUID.randomUUID().toString
      val tradeManager = system.actorOf(Props(new TradeManager(id, Some(id), eventLog)))
      tradeManager ! CreateTrade(trade); expectMsgType[CreateTradeSuccess]

      //Act
      val updatedTrade = trade.copy(buySell = "S")
      tradeManager ! UpdateTrade(updatedTrade)

      //Assert
      expectMsgPF(){
        case UpdateTradeSuccess(savedTrade) =>
          savedTrade mustBe updatedTrade

        case UpdateTradeFailure(cause) => fail
      }
    }

    "be able to retrieve all trades when called with `RetrieveTrades`" in {
      //Arrange
      val trade1 = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val trade2 = Trade(tradeDate = DateTime.now, buySell = "S", assetId = 1, quantity = 50, price = 10.2)
      val id = UUID.randomUUID().toString
      val tradeManager = system.actorOf(Props(new TradeManager(id, Some(id), eventLog)))
      tradeManager ! CreateTrade(trade1); expectMsgType[CreateTradeSuccess]
      tradeManager ! CreateTrade(trade2); expectMsgType[CreateTradeSuccess]

      //Act
      tradeManager ! RetrieveTrades

      //Assert
      expectMsgPF(){
        case RetrieveTradesSuccess(trades) =>
          trades.size mustBe 2
          trades must contain(trade1)
          trades must contain(trade2)

        case _ => fail
      }
    }

    "be able to retrieve specific trade when called with `FindTrade`" in {
      //Arrange
      val trade1 = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val trade2 = Trade(tradeDate = DateTime.now, buySell = "S", assetId = 1, quantity = 50, price = 10.2)
      val id = UUID.randomUUID().toString
      val tradeManager = system.actorOf(Props(new TradeManager(id, Some(id), eventLog)))
      tradeManager ! CreateTrade(trade1); expectMsgType[CreateTradeSuccess]
      tradeManager ! CreateTrade(trade2); expectMsgType[CreateTradeSuccess]

      //Act
      tradeManager ! FindTrade(trade1.id)

      //Assert
      expectMsgPF(){
        case FindTradeSuccess(retrievedTradeOpt) =>
          retrievedTradeOpt must not be empty
          retrievedTradeOpt.get mustBe trade1

        case _ => fail
      }
    }


  }
}
