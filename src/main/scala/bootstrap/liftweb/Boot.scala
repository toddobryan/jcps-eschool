package bootstrap.liftweb

import net.liftweb._

import common._
import http._
import sitemap._

import eschool.{sites, users}
import users.model.User

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot() {
    // where to search snippet
    //LiftRules.addToPackages("eschool.sites")
    LiftRules.addToPackages("eschool.users")

    // Build SiteMap
    def sitemap = SiteMap(
      Array.concat(
        Array[ConvertableToMenu](Menu("Home") / "index"), // the simple way to declare a menu

        //sites.menus,
        users.menus
      ): _*
    )

    def sitemapMutators: (SiteMap => SiteMap) = ((siteMap: SiteMap) => siteMap)

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    LiftRules.dispatch.append(sites.Dispatch)

    // Use jQuery 1.4
    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    ResourceServer.allow {
      case "css" :: _ => true
      case "js" :: _ => true
      case "tinymce" :: _ => true
    }

    MongoConfig.init
  }
}
