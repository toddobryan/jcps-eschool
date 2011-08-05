package eschool.sites.snippet

import eschool.sites.model.{Page, Site}
import eschool.users.model.User
import net.liftweb.util.FieldError
import com.foursquare.rogue.Rogue._
import net.liftweb.common._
import net.liftweb.http.S
import xml._
import eschool.utils.snippet.EditorScreen

class AddPage(userSiteAndMaybePage: (User, Site, Option[Page])) extends EditorScreen {
  object currentUser extends ScreenVar[User](User.getCurrentOrRedirect())
  val (user: User, site: Site, maybePage: Option[Page]) = userSiteAndMaybePage
  if (currentUser.id.get != user.id.get) {
    S.error("You do not have permission to add a page to this site.")
    S.redirectTo(S.referer openOr "/index")
  }
  val pathToParent: List[String] = maybePage match {
    case Some(page) => "sites" :: page.getPath
    case None => "sites" :: user.username.get :: site.ident.get :: Nil
  }
  val parent: Either[Site, Page] = maybePage match {
    case Some(page) => Right(page)
    case None => Left(site)
  }
  object newPage extends ScreenVar[Page](Page.createRecord)

  val ident = text("Page Path: " + pathToParent.mkString("/", "/", "/"), "",
      validateIdent _,
      (s: String) => boxStrToListFieldError(Page.uniqueIdent(parent, s)))
  val name = text("Page Name", "",
      validatePage _,
      (s: String) => boxStrToListFieldError(Page.uniqueName(parent, s)))
  val content = mceTextarea("Content", "", 30, 80)

  def finish() {
    newPage.ident(ident.get).name(name.get).content(XML.loadString("<dummy>" + content.get + "</dummy>").child).save(true)
    parent match {
      case Left(site) => site.pages(site.pages.get + (ident.get -> newPage.id.get)).save(true)
      case Right(page) => page.pages(page.pages.get + (ident.get -> newPage.id.get)).save(true)
    }

  }
}