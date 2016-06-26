package com.kkanojia.rpe.models

import java.util.UUID

import org.joda.time.DateTime

case class Trade(id: UUID,
                 tradeDate: DateTime,
                 buySell: String,
                 assetId: Int,
                 quantity: Int,
                 price: BigDecimal)

object Trade {

  def apply(tradeDate: DateTime,
            buySell: String,
            assetId: Int,
            quantity: Int,
            price: BigDecimal): Trade = {
    new Trade(UUID.randomUUID(), tradeDate, buySell, assetId, quantity, price)
  }
}