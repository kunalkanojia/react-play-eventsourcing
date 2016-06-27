package com.kkanojia.example.actors

import java.util.UUID
import scala.collection.immutable.HashSet
import scala.collection.mutable

import akka.actor.ActorRef
import com.kkanojia.example.models.Trade
import CumulativeTradeViewActor.{UnWatchTrades, WatchTrades}
import TradeActor.{TradeCreated, TradeUpdated}
import com.rbmhtechnology.eventuate.EventsourcedView

object CumulativeTradeViewActor {

  val ID = "464788cb-58aa-4dc6-8dce-703a456c238a"
  val NAME = "cumulative_trade_view"

  case object WatchTrades
  case object UnWatchTrades
}

class CumulativeTradeViewActor(override val id: String,
                               override val aggregateId: Option[String],
                               override val eventLog: ActorRef) extends EventsourcedView {

  protected[this] var watchers = HashSet.empty[ActorRef]

  private val trades = mutable.Map[UUID, Trade]()

  override def onCommand: Receive = {

    case WatchTrades =>
      watchers = watchers + sender

    case UnWatchTrades =>
      watchers = watchers - sender

  }

  override def onEvent: Receive = {
    case TradeCreated(trade) =>
      trades(trade.id) = trade
      watchers.foreach(_ ! TradeCreated(trade))

    case TradeUpdated(trade) =>
      trades(trade.id) = trade
      watchers.foreach(_ ! TradeUpdated(trade))

  }
}
