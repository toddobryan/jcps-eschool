package eschool.sites.snippet

import net.liftweb.util.Helpers._
import eschool.users.model.User
import xml.NodeSeq
import eschool.sites.model.{QSite, Site}
import eschool.utils.Helpers._
import net.liftweb.http.S
import net.liftweb.common.{Full, Box}
import bootstrap.liftweb.DataStore

class SiteList(user: User) {
  def render: (NodeSeq => NodeSeq) = {
    val currentUser_? : Boolean = User.getCurrent.isDefined &&
        User.getCurrent.get.id == user.id
    val header: String = (if (currentUser_?) {
      "Your"
    } else {
      user.displayName + "'s"
    }) + " Sites"
    val sites = DataStore.pm.query[Site].filter(QSite.candidate.owner.eq(user)).executeList()
    val userHasSites: String = (if (currentUser_?) "You have" else user.displayName + " has") +
      (if (sites.isEmpty) " no sites." else " the following " + pluralizeInformal(sites.length, "site") + ":")
    val listOfSites = if (sites.isEmpty) {
      <br/>
    } else {
      <ul>
      { sites.flatMap(
        (s: Site) =>
        <li><a href={ "/sites/%s/%s".format(user.username, s.ident)}>{ s.name }</a></li>
      )}
      </ul>
    }
    val createSite = if (currentUser_?) {
      <p><a href="/sites/createSite" class="createLink">Create a New Site</a></p>
    } else {
      NodeSeq.Empty
    }
    ".header *" #> header &
    ".userHasSites" #> userHasSites &
    ".listOfSites" #> listOfSites &
    ".createSite" #> createSite
  }
}