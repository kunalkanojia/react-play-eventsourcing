package com.kkanojia.example.services

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

import com.kkanojia.example.forms.TradeForm.TradeFormData
import org.joda.time.DateTime
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}

class TradeServiceSpec extends PlaySpec with OneAppPerSuite with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  "A Trade Service" must {
    val userService = app.injector.instanceOf[UserService]
    val tradeService = app.injector.instanceOf[TradeService]

    "be abe to create trade for a user" in {
      //Arrange
      val user = Await.result(userService.createUser(s"phil@${Random.nextInt(999999)}"), 3 seconds).get
      val tradeData = TradeFormData(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)

      //Act
      val tradePromise = tradeService.createTrade(user, tradeData)

      //Assert
      whenReady(tradePromise) {
        case Some(savedTrade) =>
          savedTrade.tradeDate mustBe tradeData.tradeDate
          savedTrade.buySell mustBe tradeData.buySell
          savedTrade.assetId mustBe tradeData.assetId
          savedTrade.quantity mustBe tradeData.quantity
          savedTrade.price mustBe tradeData.price

        case None => fail
      }
    }

    "be able to update a specific trade for a user" in {
      //Arrange
      val user = Await.result(userService.createUser(s"phil@${Random.nextInt(999999)}"), 3 seconds).get
      val tradeData = TradeFormData(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val originalTrade = Await.result(tradeService.createTrade(user, tradeData), 3 second).get

      //Act
      val updatedTradeData = tradeData.copy(buySell = "S")
      val tradeUpdatePromise = tradeService.updateTrade(user, originalTrade, updatedTradeData)

      //Assert
      whenReady(tradeUpdatePromise) {
        case Some(savedTrade) =>
          savedTrade.tradeDate mustBe updatedTradeData.tradeDate
          savedTrade.buySell mustBe updatedTradeData.buySell
          savedTrade.assetId mustBe updatedTradeData.assetId
          savedTrade.quantity mustBe updatedTradeData.quantity
          savedTrade.price mustBe updatedTradeData.price

        case None => fail
      }
    }

    "be able to retrieve all trades for a user" in {
      //Arrange
      val user = Await.result(userService.createUser(s"phil@${Random.nextInt(999999)}"), 3 seconds).get
      val tradeData1 = TradeFormData(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeData2 = TradeFormData(tradeDate = DateTime.now, buySell = "S", assetId = 2, quantity = 50, price = 23.2)
      val trade1 = Await.result(tradeService.createTrade(user, tradeData1), 3 second).get
      val trade2 = Await.result(tradeService.createTrade(user, tradeData2), 3 second).get

      //Act
      val tradesPromise = tradeService.retrieveTrades(user)

      //Assert
      whenReady(tradesPromise) { retrievedTrades =>
        retrievedTrades.size mustBe 2
        retrievedTrades must contain(trade1)
        retrievedTrades must contain(trade2)
      }

    }

    "be able to retrieve a particular trade for a user" in {
      //Arrange
      val user = Await.result(userService.createUser(s"phil@${Random.nextInt(999999)}"), 3 seconds).get
      val tradeData1 = TradeFormData(tradeDate = DateTime.now, buySell = "B", assetId = 1, quantity = 100, price = 20.2)
      val tradeData2 = TradeFormData(tradeDate = DateTime.now, buySell = "S", assetId = 2, quantity = 50, price = 23.2)
      val trade1 = Await.result(tradeService.createTrade(user, tradeData1), 3 second).get
      Await.result(tradeService.createTrade(user, tradeData2), 3 second).get

      //Act
      val tradePromise = tradeService.retrieveTrade(user, trade1.id)

      //Assert
      whenReady(tradePromise) {
        case Some(retrievedTrade) =>
          retrievedTrade mustBe trade1

        case None => fail
      }
    }

    "return `None` when trade is not found" in {
      //Arrange
      val user = Await.result(userService.createUser(s"phil@${Random.nextInt(999999)}"), 3 seconds).get

      //Act
      val tradePromise = tradeService.retrieveTrade(user, UUID.randomUUID().toString)

      //Assert
      whenReady(tradePromise) {
        case None =>

        case Some(_) => fail
      }
    }
  }


}
