package wandledi.scala.example.controllers

import wandledi.java.DefaultRoute
import wandledi.scala.PageController
import wandledi.scala.example.pages.HomePage
import wandledi.scala.example.models.BlogEntry
import wandledi.scala.example.models.Comment

class Home extends PageController {

  protected val page = new HomePage

  override def getPage = page

  override def beforeAction() {
    val msg = flash.getAs[String]("msg")
    page.loggedIn = loggedIn
    page.beforeAction(msg, DefaultRoute.getURI("home", "index"))
  }

  def index() {
    page.index(BlogEntry.findAll.toSeq)
  }

  def login() {
    if (isGetRequest) {
      page.login()
    } else {
      val user = params("user")
      val pwd = params("password")
      if ("blogger".equals(user) && "reggolb".equals(pwd)) {
        loggedIn = true
        flash += "msg" -> "Login successful"
      } else {
        flash += "msg" -> "Login failed"
      }
      this.redirectTo("home")
    }
  }

  def post() {
    if (isGetRequest) {
      page.post()
    } else {
      val entry = new BlogEntry(params)
      if (entry.validate) {
        entry.save()
        flash += "msg" -> "Entry saved"
        this.redirectTo("home")
      } else {
        page.post(Some("Invalid field"), params.get("author"), params.get("title"), params.get("content"))
      }
    }
  }

  def comments() {
    val entry = BlogEntry.find(params("id").toLong)
    page.comments(entry)
  }

  def comment() {
    database.beginTransaction()
    val entry = BlogEntry.find(params("id").toLong)
    if (isGetRequest) {
      page.comment(entry)
    } else {
      val comment = new Comment(params)
      if (comment.validate) {
        entry.comments.add(comment)
        if (database.commitTransaction()) {
          page.message = "Comment saved"
        } else {
          page.message = "Could not save comment"
        }
        page.comments(entry)
      } else {
        page.comment(Some("Missing field"), entry, params.get("author"),
            params.get("email"), params.get("content"))
      }
    }
  }

  def deleteComment() {
    database.beginTransaction()
    val entry = BlogEntry.find(params("bid").toLong)
    val comment = Comment.find(params("id").toLong)
    entry.comments.remove(comment)
    if (database.commitTransaction()) {
      flash += "msg" -> "Comment deleted"
    } else {
      flash += "msg" -> "Could not delete comment"
    }
    this.redirectTo("home", "comments", params("bid").toLong)
  }
  
  protected def loggedIn: Boolean = {
    val login = session.getAs[Boolean]("login")
    if (login.isDefined) login.get else false
  }

  protected def loggedIn_=(login: Boolean) {
    session += "login" -> login
  }

  protected def isGetRequest = request.getMethod().equalsIgnoreCase("get")
}
