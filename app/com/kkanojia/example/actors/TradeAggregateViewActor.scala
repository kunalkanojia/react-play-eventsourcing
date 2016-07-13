package com.kkanojia.example.actors

import scala.collection.immutable.HashSet
import scala.collection.mutable

import akka.actor.ActorRef
import com.kkanojia.example.actors.TradeActor.{TradeCreated, TradeUpdated}
import com.kkanojia.example.actors.TradeAggregateViewActor.{UnWatchTrades, WatchTrades}
import com.kkanojia.example.models.Trade
import com.rbmhtechnology.eventuate.EventsourcedView

object TradeAggregateViewActor {

  val ID = "464788cb-58aa-4dc6-8dce-703a456c238a"
  val NAME = "trade_view_aggregate"

  case object WatchTrades

  case object UnWatchTrades

}

class TradeAggregateViewActor(override val id: String,
  override val eventLog: ActorRef
) extends EventsourcedView {

  protected[this] var watchers = HashSet.empty[ActorRef]

  private val trades = mutable.Map[String, Trade]()

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
