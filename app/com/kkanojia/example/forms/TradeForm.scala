package com.kkanojia.example.forms

import org.joda.time.DateTime

import play.api.data.Form
import play.api.data.Forms._


object TradeForm {

  val tradeForm = {
    Form(
      mapping(
        "trade_date" -> jodaDate("dd/MM/yyyy"),
        "buy_sell" -> nonEmptyText,
        "asset_id" -> number,
        "quantity" -> number,
        "price" -> bigDecimal
      )(TradeFormData.apply)(TradeFormData.unapply)
    )
  }

  case class TradeFormData(tradeDate: DateTime,
                           buySell: String,
                           assetId: Int,
                           quantity: Int,
                           price: BigDecimal
                                )

}
