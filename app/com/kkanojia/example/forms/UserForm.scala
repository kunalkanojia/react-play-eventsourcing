package com.kkanojia.example.forms

import play.api.data.Form
import play.api.data.Forms._

object UserForm {

  val userNameForm = Form(single("user_email" -> email))

}
