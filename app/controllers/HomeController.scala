package controllers

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

import com.kkanojia.rpe.forms.UserForm._
import com.kkanojia.rpe.models.User
import com.kkanojia.rpe.services.UserService
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import controllers.routes._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
                                webJarAssets: WebJarAssets,
                                userService: UserService,
                                override val messagesApi: MessagesApi
                              )(implicit exec: ExecutionContext) extends Controller with I18nSupport {

  implicit val formats = DefaultFormats


  def index = Action { implicit request =>
    Ok(views.html.index(webJarAssets, userNameForm, userNameForm))
  }

  def handleUser = Action.async { implicit request =>
    userNameForm.bindFromRequest.fold(
      errorForm => {
        println(userNameForm.error("user_email"))
        Future {
          Ok(views.html.index(webJarAssets, errorForm, userNameForm))
        }
      },
      userEmail => {
        userService.createUser(userEmail).map {
          case Some(user) => Redirect(TradeController.dashboard()).withSession("user" -> write[User](user))
          case None => Redirect(HomeController.index()).flashing("error" -> "Error creating user, Try another email.")
        }
      }
    )
  }

  def handleLogin = Action.async { implicit request =>
    userNameForm.bindFromRequest.fold(
      errorForm => {
        println(userNameForm.error("user_email"))
        Future {
          Ok(views.html.index(webJarAssets, userNameForm, errorForm))
        }
      },
      userEmail => {
        userService.retrieveUser(userEmail).map {
          case Some(user) => Redirect(TradeController.dashboard()).withSession("user" -> write[User](user))
          case None => Redirect(HomeController.index()).flashing("error" -> "User not found.")
        }
      }
    )
  }
}
