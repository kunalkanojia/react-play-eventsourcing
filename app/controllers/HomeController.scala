package controllers

import java.util.UUID
import javax.inject._

import play.api._
import play.api.mvc._
import play.api.libs.json.{JsArray, Json}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
 webJarAssets: WebJarAssets
) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index(webJarAssets))
  }

  def dashboard = Action {
    Ok(views.html.dashboard(webJarAssets))
  }

  // The json keys. The 'id' field was added as without it we would get a warning like this:
  // Warning: Each child in an array or iterator should have a unique "key" prop. Check the render method of CommentList. See https://fb.me/react-warning-keys for more information.
  val JSON_KEY_COMMENTS = "comments"
  val JSON_KEY_AUTHOR = "author"
  val JSON_KEY_TEXT = "text"
  val JSON_KEY_ID = "id"

  // Initialise the comments list
  var commentsJson: JsArray = Json.arr(
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Pete Hunt", JSON_KEY_TEXT -> "This is one comment"),
    Json.obj(JSON_KEY_ID -> UUID.randomUUID().toString, JSON_KEY_AUTHOR -> "Jordan Walke", JSON_KEY_TEXT -> "This is *another* comment")
  )

  // Returns the comments list
  def comments = Action {
    Ok(commentsJson)
  }

  // Adds a new comment to the list and returns it
  def comment(author: String, text: String) = Action {
    val newComment = Json.obj(
      JSON_KEY_ID -> UUID.randomUUID().toString,
      JSON_KEY_AUTHOR -> author,
      JSON_KEY_TEXT -> text)
    commentsJson = commentsJson :+ newComment
    Ok(newComment)
  }

}
