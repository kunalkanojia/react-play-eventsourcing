package controllers

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

import com.kkanojia.example.forms.TradeForm
import com.kkanojia.example.models.User
import TradeForm._
import com.kkanojia.example.services.{TradeService, UserService}
import com.kkanojia.example.services.TradeService
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

import controllers.routes._

class TradeController @Inject()(
                                 webJarAssets: WebJarAssets,
                                 userService: UserService,
                                 tradeService: TradeService,
                                 override val messagesApi: MessagesApi
                               )(implicit exec: ExecutionContext) extends Controller with I18nSupport {

  implicit val formats = DefaultFormats

  def dashboard = Action.async { implicit request =>
    request.session.get("user") match {
      case Some(userJson) =>
        val user = read[User](userJson)
        val tradesFuture = tradeService.retrieveTrades(user)
        tradesFuture.map { trades =>
          Ok(views.html.dashboard(webJarAssets, Some(user.email), trades))
        }

      case None =>
        Future {
          Redirect(HomeController.index()).flashing("error" -> "Oops, unable to get user")
        }
    }
  }

  def create = Action { implicit request =>
    request.session.get("user") match {
      case Some(userJson) =>
        val user = read[User](userJson)
        Ok(views.html.createTrade(webJarAssets, Some(user.email), tradeForm))

      case None =>
        Redirect(HomeController.index()).flashing("error" -> "Oops, unable to get user")
    }
  }

  def handleCreate = Action.async { implicit request =>
    request.session.get("user") match {
      case Some(userJson) =>
        val user = read[User](userJson)
        tradeForm.bindFromRequest.fold(
          errorForm => Future {
            Ok(views.html.createTrade(webJarAssets, Some(user.email), errorForm))
          },
          data => {
            tradeService.createTrade(user, data).map {
              case Some(trade) => Redirect(TradeController.dashboard()).flashing("success" -> "Trade Created")
              case None => Redirect(TradeController.dashboard()).flashing("error" -> "Oops, check log")
            }
          }
        )

      case None =>
        Future {
          Redirect(HomeController.index()).flashing("error" -> "Oops, unable to get user")
        }
    }
  }

  def edit(tradeId: String) = Action.async { implicit request =>
    request.session.get("user") match {
      case Some(userJson) =>
        val user = read[User](userJson)
        tradeService.retrieveTrade(user, tradeId).map {
          case Some(trade) =>
            val formData = TradeFormData(trade.tradeDate, trade.buySell, trade.assetId, trade.quantity, trade.price)
            Ok(views.html.createTrade(webJarAssets, Some(user.email), tradeForm.fill(formData), Some(trade.id.toString)))

          case None => Redirect(TradeController.dashboard()).flashing("error" -> "Unable to find trade.")
        }

      case None =>
        Future {
          Redirect(HomeController.index()).flashing("error" -> "Oops, unable to get user")
        }
    }
  }

  def handleEdit(tradeId: String) = Action.async { implicit request =>
    request.session.get("user") match {
      case Some(userJson) =>
        val user = read[User](userJson)
        tradeForm.bindFromRequest.fold(
          errorForm => Future {
            Ok(views.html.createTrade(webJarAssets, Some(user.email), errorForm, Some(tradeId)))
          },
          data => {
            tradeService.retrieveTrade(user, tradeId).flatMap {
              case Some(trade) =>
                tradeService.updateTrade(user, trade, data).map {
                  case Some(savedTrade) => Redirect(TradeController.dashboard()).flashing("success" -> "Trade Updated")
                  case None => Redirect(TradeController.dashboard()).flashing("error" -> "Oops, check log")
                }
              case None =>
                Future {
                  Redirect(TradeController.dashboard()).flashing("error" -> "Unable to find trade.")
                }
            }

          }
        )

      case None =>
        Future {
          Redirect(HomeController.index()).flashing("error" -> "Oops, unable to get user")
        }
    }
  }

}
