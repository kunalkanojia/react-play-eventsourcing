package com.kkanojia.example.actors

import scala.util.{Failure, Success}

import akka.actor.ActorRef
import com.kkanojia.example.models.Trade
import TradeActor._
import com.rbmhtechnology.eventuate.EventsourcedActor

object TradeActor {

  //Commands
  case class CreateTrade(trade: Trade)
  case class UpdateTrade(trade: Trade)

  //Replies
  case class CreateTradeSuccess(trade: Trade)
  case class CreateTradeFailure(cause: Throwable)
  case class UpdateTradeSuccess(trade: Trade)
  case class UpdateTradeFailure(cause: Throwable)

  //Events
  case class TradeCreated(trade: Trade)
  case class TradeUpdated(trade: Trade)

}

class TradeActor(override val id: String,
                 override val aggregateId: Option[String],
                 override val eventLog: ActorRef,
                 managerId: String)
  extends EventsourcedActor {

  private var tradeOpt: Option[Trade] = None

  override def onCommand: Receive = {

    case CreateTrade(trade) =>
      persist(TradeCreated(trade), Set(managerId, CumulativeTradeViewActor.ID)) {
        case Success(evt) =>
          sender() ! CreateTradeSuccess(trade)
        case Failure(cause) =>
          sender() ! CreateTradeFailure(cause)
      }

    case UpdateTrade(trade) =>
      persist(TradeUpdated(trade), Set(managerId, CumulativeTradeViewActor.ID)) {
        case Success(evt) =>
          sender() ! UpdateTradeSuccess(trade)
        case Failure(cause) =>
          sender() ! UpdateTradeFailure(cause)
      }
  }

  override def onEvent: Receive = {
    case TradeCreated(trade) =>
      tradeOpt = Some(trade)

    case TradeUpdated(trade) =>
      tradeOpt = Some(trade)
  }
}
