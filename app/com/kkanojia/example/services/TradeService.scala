package com.kkanojia.example.services

import javax.inject.Inject
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

import akka.actor.{ActorSelection, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.kkanojia.example.actors.{TradeActor, TradeManager, UserManager}
import TradeActor._
import TradeManager.{FindTrade, FindTradeSuccess, RetrieveTrades, RetrieveTradesSuccess}
import com.kkanojia.example.forms.TradeForm
import TradeForm.TradeFormData
import com.kkanojia.example.models.{Trade, User}
import com.kkanojia.example.models.Trade

class TradeService @Inject()(system: ActorSystem)(implicit ec: ExecutionContext) {

  implicit val timeout = Timeout(15 seconds)

  def manager(user: User): ActorSelection = {
    system.actorSelection(s"user/${UserManager.NAME}/user_${user.id.toString}/tm_${user.id.toString}")
  }

  def createTrade(user: User, data: TradeFormData): Future[Option[Trade]] = {
    val trade = Trade(data.tradeDate, data.buySell, data.assetId, data.quantity, data.price)
    manager(user) ? CreateTrade(trade) map {
      case CreateTradeSuccess(savedTrade) => Some(savedTrade)
      case CreateTradeFailure(cause) => None
    }
  }

  def updateTrade(user: User, trade: Trade,  data: TradeFormData): Future[Option[Trade]] = {
    val updatedTrade = Trade(trade.id, data.tradeDate, data.buySell, data.assetId, data.quantity, data.price)
    manager(user) ? UpdateTrade(updatedTrade) map {
      case UpdateTradeSuccess(savedTrade) => Some(savedTrade)
      case UpdateTradeFailure(cause) => None
    }
  }

  def retrieveTrades(user: User): Future[Seq[Trade]] = {
    manager(user) ? RetrieveTrades map {
      case RetrieveTradesSuccess(trades) => trades
      case _ => Seq()
    }
  }

  def retrieveTrade(user: User, tradeId: String): Future[Option[Trade]] = {
    manager(user) ? FindTrade(tradeId) map {
      case FindTradeSuccess(tradeOpt) => tradeOpt
      case _ => None
    }
  }

}
