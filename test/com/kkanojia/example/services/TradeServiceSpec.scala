package com.kkanojia.example.services

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}

class TradeServiceSpec  extends PlaySpec with OneAppPerTest with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "A Trade Service" must {

    "be abe to create trade for a user" in {
      //Arrange
      //Act
      //Assert
    }

    "be able to update a specific trade for a user" in {
      //Arrange
      //Act
      //Assert
    }

    "be able to retrieve all trades for a user" in {
      //Arrange
      //Act
      //Assert
    }

    "be able to retrieve a particular trade for a user" in {
      //Arrange
      //Act
      //Assert
    }


  }


}
