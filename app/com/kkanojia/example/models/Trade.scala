package com.kkanojia.example.models

import java.util.UUID

import org.joda.time.DateTime

case class Trade(id: String,
  tradeDate: DateTime,
  buySell: String,
  assetId: Int,
  quantity: Int,
  price: BigDecimal
)

object Trade {

  def apply(tradeDate: DateTime,
    buySell: String,
    assetId: Int,
    quantity: Int,
    price: BigDecimal): Trade = {
    new Trade(UUID.randomUUID().toString, tradeDate, buySell, assetId, quantity, price)
  }
}