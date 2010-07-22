package wandledi.scala.example.controllers

import wandledi.scala.PageController
import wandledi.scala.example.pages.HomePage

class Home extends PageController {

  val page = new HomePage

  def getPage = page

  def index() {

    page.index(new java.util.Date().toString())
  }  
}
