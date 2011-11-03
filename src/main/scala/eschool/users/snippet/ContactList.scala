package eschool.users.snippet

import net.liftweb.util._
import Helpers._
import eschool.users.model.{QUser, User}
import xml.NodeSeq
import bootstrap.liftweb.DataStore

object ContactList {
  def render = ".userRow *" #> allUsers().map(renderUser(_))

  def renderUser(user: User) = {
    val email = user.getEmail match {
      case null => NodeSeq.Empty
      case address: String => <a href={ "mailto:" + address }>{ address }</a>
    }
    ".name *" #> user.formalName &
    ".email *" #> email
  }

  def allUsers(): List[User] = {
    val cand = QUser.candidate
    DataStore.pm.query[User].orderBy(cand.last, cand.first, cand.middle).executeList()
  }
}