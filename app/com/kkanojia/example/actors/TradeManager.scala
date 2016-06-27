package com.kkanojia.example.actors

import java.util.UUID
import scala.collection.mutable

import akka.actor.{ActorRef, Props}
import com.kkanojia.example.models.Trade
import com.rbmhtechnology.eventuate.EventsourcedView

object TradeManager{

  //Command
  case object RetrieveTrades
  case class FindTrade(tradeId: String)

  case class RetrieveTradesSuccess(trades: Seq[Trade])
  case class FindTradeSuccess(tradeOpt: Option[Trade])

}

class TradeManager(override val id: String,
                   override val aggregateId: Option[String],
                   override val eventLog: ActorRef) extends EventsourcedView {

  import TradeActor._
  import TradeManager._

  private val userTrades = mutable.Map[UUID, Trade]()

  override def onCommand: Receive = {

    case CreateTrade(trade) =>
      getTradeActor(trade.id.toString) forward CreateTrade(trade)

    case RetrieveTrades =>
      sender() ! RetrieveTradesSuccess(userTrades.values.toSeq)

    case FindTrade(tradeId: String) =>
      sender() ! FindTradeSuccess(userTrades.get(UUID.fromString(tradeId)))

    case UpdateTrade(trade) =>
      getTradeActor(trade.id.toString) forward UpdateTrade(trade)

  }

  override def onEvent: Receive = {
    case TradeCreated(trade) =>
      userTrades(trade.id) = trade

    case TradeUpdated(trade) =>
      userTrades(trade.id) = trade
  }

  private def getTradeActor(tradeId: String) : ActorRef =  {
    val name = s"trade_$tradeId"
    context.child(name) match {
      case Some(actorRef) => actorRef
      case None => context.actorOf(Props(new TradeActor(tradeId, Some(tradeId), eventLog, aggregateId.getOrElse(""))), name)
    }
  }
}
