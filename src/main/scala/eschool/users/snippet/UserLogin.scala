package eschool.users.snippet

import net.liftweb.common._
import net.liftweb.http.{S, LiftScreen}

import eschool.users.model.User
import xml.{Text, Elem}
import net.liftweb.util.FieldError

object UserLogin extends LiftScreen {
  object user extends ScreenVar[Box[User]](Empty)
  val username = field("Username", "")
  val passwd = password("Password", "")

  def authenticateUser(): List[FieldError] = {
    user(User.authenticate(username.get, passwd.get))
    if (user.get.isDefined) Nil else Text("Incorrect username or password.")
  }

  override def validations = authenticateUser _ +: super.validations

  def finish() {
    // user must be Full if we get here
    User.login(user.get.open_!)
  }
}
