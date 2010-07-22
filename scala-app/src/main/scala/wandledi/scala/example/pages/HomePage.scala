package wandledi.scala.example.pages

import wandledi.scala.html._

class HomePage extends Page {

  def index(msg: String) {

    get("body").insertLast(msg)
  }
}
