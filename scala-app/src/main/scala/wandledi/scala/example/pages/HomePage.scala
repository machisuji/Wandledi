package wandledi.scala.example.pages

import wandledi.scala.example.models.BlogEntry
import wandledi.scala.example.models.Comment
import wandledi.scala.example.models.JavaBlogEntry
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
    get(".homeLink").setAttribute("href", homeLink)
    get("#right").insert(msg.get) unless msg.isEmpty
    if (loggedIn) {
      get("href" -> "login").setAttribute("href", "post")
      get("button").replace(true, "Post Entry")
    }
  }

  def node = <any name="author"/>

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
    get(".entry").changeAttribute("class", "form $val")
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
      } else {
        if (index == comments.size - 1) { // comment form
          e.setAttribute("id", "last")
          e.changeAttribute("class", "form $val")
        } else { // comment
          produceComment(e, comment)
          includeDeleteButton(e, comment, entry.getId.longValue) provided loggedIn
          e.changeAttribute("class", "comment $val")
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
    get(".entry").changeAttribute("class", "form $val")
    get("#right").insert(msg.get) unless msg.isEmpty
  }

  def comment(entry: BlogEntry) {
    comment(None, entry, None, None, None)
  }

  private def includeDeleteButton(e: SelectableElement, comment: Comment, bid: Long) {
    e.get("br").includeFile("/home/delete.xhtml") { page =>
      page.get("a").setAttribute("href", linkToId("home", "deleteComment", comment.getId()) + "?bid=" + bid)
    }
  }

  private def includeInEntry(file: String, selectable: Selectable)(magic: (Selectable) => Unit) {
    selectable.get("p").includeFile(file)(magic)
    selectable.get("span").hide()
    selectable.get("br").hide()
  }

  private def produceEntry(e: SelectableElement, entry: BlogEntry) {
    e.get(".heading").replace(true, entry.getTitle)
    e.get(".user").replace(true, entry.getAuthor)
    e.get(".date").replace(true, entry.getDate.toString)
    e.get(".text").replace(true, entry.getContent)
    e.get(".footer").insert(entry.getComments.size + " ")
    e.get("href" -> "comments").setAttribute("href", linkToId("home", "comments", entry.getId))
  }

  private def produceComment(e: SelectableElement, comment: Comment) {
    e.get(".heading").replace(true, "Comment from " + comment.getAuthor)
    e.get(".subheading").replace(true, comment.getDate.toString)
    e.get(".text").replace(true, comment.getContent)
    e.get(".footer").hide()
  }
}
