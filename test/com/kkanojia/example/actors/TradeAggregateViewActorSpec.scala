package com.kkanojia.example.actors

import java.util.UUID

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._

import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.kkanojia.example.actors.TradeActor._
import com.kkanojia.example.actors.TradeAggregateViewActor.WatchTrades
import com.kkanojia.example.models.Trade
import com.rbmhtechnology.eventuate.ReplicationEndpoint
import com.rbmhtechnology.eventuate.ReplicationEndpoint._
import com.rbmhtechnology.eventuate.log.leveldb.LeveldbEventLog
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class TradeAggregateViewActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with MustMatchers {


  def this() = this(ActorSystem("TradeAggregateViewActorSpec"))

  val endpoint = ReplicationEndpoint(id => LeveldbEventLog.props(logId = "TradeAggregateViewActorSpec"))(_system)
  val eventLog = endpoint.logs(DefaultLogName)

  "A Trade Aggregate View Actor" must {

    "receive all trade create messages and publish it to watchers" in {
      //Arrange
      val id = UUID.randomUUID().toString
      val aggregateActor = system.actorOf(Props(new TradeAggregateViewActor(id, eventLog)))
      val probe = new TestProbe(system)
      val wSUserActor = system.actorOf(Props(new ProbeWrapper(probe)))
      aggregateActor.tell(WatchTrades, wSUserActor)

      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeActor = system.actorOf(Props(new TradeActor(trade.id, Some(trade.id), eventLog, id)))

      //Act
      tradeActor ! CreateTrade(trade); expectMsgType[CreateTradeSuccess]

      //Assert
      val webSocketUserMessage = probe.receiveOne(1 second)
      webSocketUserMessage mustBe a [TradeCreated]
    }

    "receive all trade update messages and publish it to watchers" in {
      //Arrange
      val id = UUID.randomUUID().toString
      val aggregateActor = system.actorOf(Props(new TradeAggregateViewActor(id, eventLog)))
      val probe = new TestProbe(system)
      val wSUserActor = system.actorOf(Props(new ProbeWrapper(probe)))
      aggregateActor.tell(WatchTrades, wSUserActor)
      val trade = Trade(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeActor = system.actorOf(Props(new TradeActor(trade.id, Some(trade.id), eventLog, id)))

      //Act
      tradeActor ! UpdateTrade(trade); expectMsgType[UpdateTradeSuccess]

      //Assert
      val webSocketUserMessage = probe.receiveOne(1 second)
      webSocketUserMessage mustBe a [TradeUpdated]
    }

  }

}
