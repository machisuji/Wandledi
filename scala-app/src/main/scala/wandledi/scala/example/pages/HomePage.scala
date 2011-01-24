package wandledi.scala.example.pages

import wandledi.scala.example.models.BlogEntry
import wandledi.scala.example.models.Comment
import wandledi.scala.html.SelectableElement
import wandledi.scala.html._
import wandledi.java.Switchboard.linkToUri
import wandledi.core.Attribute
import wandledi.core.Scroll
import wandledi.java.Switchboard.linkToId
import wandledi.scala.Implicits._
import collection.JavaConversions._

class HomePage extends Page {

  var loggedIn: Boolean = false

  def beforeAction(msg: Option[String], homeLink: String) {	  
    get("rel" -> "stylesheet").setAttribute("href", linkToUri("/css/main.css"))
    get(".homelink").setAttribute("href", homeLink)
    get("#right").insert(msg.get) unless msg.isEmpty
    if (loggedIn) {
      get("href" -> "login").setAttribute("href", "post")
      get("button").replace(true, "Post Entry")
    }
  }

  def index(entries: Seq[BlogEntry]) {
    if (entries.isEmpty) {
      get(".entry").setAttribute("id", "last")
      get(".heading").replace(true, "No Entries yet")
      get(".text").replace(true, "There are no entries yet. Why don't you start writing one?")
      get(".footer").hide()
    } else {
      get(".entry").foreachWithIndexIn(entries) { (e, entry, index) =>
        e.setAttribute("id", "last") provided index == entries.size - 1
        produceEntry(e, entry)
      }
    }
  }

  def login() {
    setFile("/home/index.xhtml");
    includeInEntry("/home/login.xhtml", this)((scroll)=>{})
  }
  
  def post(msg: Option[String], author: Option[String], title: Option[String], content: Option[String]) {
    setFile("/home/index.xhtml")
    includeInEntry("/home/post.xhtml", this) { page =>
      page.get("name" -> "author").setAttribute("value", author.get) unless author.isEmpty
      page.get("name" -> "title").setAttribute("value", title.get) unless title.isEmpty
      page.get("name" -> "content").replace(true, content.get) unless content.isEmpty
    }
    get(".entry").changeAttribute("class")("form " + _)
  }

  def post() {
    post(None, None, None, None)
  }

  def comments(entry: BlogEntry) {
    val comments = (new Comment :: entry.getComments.toList) :+ new Comment
    setFile("/home/index.xhtml")
    get(".entry").foreachWithIndexIn(comments) { (e, comment, index) =>
      if (index == 0) { // blog entry
        produceEntry(e, entry)
        e.get(".footer").hide()
      } else {
        if (index == comments.size - 1) { // comment form
          e.setAttribute("id", "last")
          e.changeAttribute("class")("form " + _)
          includeInEntry("/home/comment.xhtml", e) { page => /* include unchanged */ }
        } else { // comment
          produceComment(e, comment)
          includeDeleteButton(e, comment, entry.getId.longValue) provided loggedIn
          e.changeAttribute("class")("comment " + _)
        }
      }
    }
  }

  def comment(msg: Option[String], entry: BlogEntry,
              author: Option[String], email: Option[String], content: Option[String]) {
    setFile("/home/index.xhtml")
    includeInEntry("/home/comment.xhtml", this) { page =>
      page.get("name" -> "author").setAttribute("value", author.get) unless author.isEmpty
      page.get("name" -> "email").setAttribute("value", email.get) unless email.isEmpty
      page.get("name" -> "content").replace(true, content.get) unless content.isEmpty
    }
    get(".entry").changeAttribute("class")("form " + _)
    get("#right").insert(msg.get) unless msg.isEmpty
  }

  def comment(entry: BlogEntry) {
    comment(None, entry, None, None, None)
  }

  def message_=(msg: String): Unit = get("#right").insert(msg)

  /**Always returns null.
   */
  def message: String = null

  private def includeDeleteButton(e: SelectableElement, comment: Comment, bid: Long) {
    e.get("br").includeFile("/home/delete.xhtml") { page =>
      page.get("a").setAttribute("href", linkToId("home", "deleteComment", comment.id) + "?bid=" + bid)
    }
  }

  private def includeInEntry(file: String, selectable: ScalaSelectable)(magic: (ScalaSelectable) => Unit) {
    selectable.get("p").includeFile(file)(magic)
    selectable.get("span").hide()
    selectable.get("br").hide()
  }

  private def produceEntry(e: SelectableElement, entry: BlogEntry) {
    e.get(".heading").replace(true, entry.title)
    e.get(".user").replace(true, entry.author)
    e.get(".date").replace(true, if (entry.date != null) entry.date.toString else "???")
    e.get(".text").replace(true, entry.content)
    e.get("href" -> "comments").setAttribute("href", linkToId("home", "comments", entry.id))
    if (entry.comments.size > 0) {
      e.get(".footer").insert(entry.comments.size + " ")
    } else {
      e.get("href" -> "comments").replace(true, "add comment")
    }
  }

  private def produceComment(e: SelectableElement, comment: Comment) {
    e.get(".heading").replace(true, "Comment from " + comment.author)
    e.get(".subheading").replace(true, comment.date.toString)
    e.get(".text").replace(true, comment.content)
    e.get(".footer").hide()
  }
}
