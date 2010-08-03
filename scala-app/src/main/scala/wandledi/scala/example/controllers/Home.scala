package wandledi.scala.example.controllers

import wandledi.scala.PageController
import wandledi.scala.example.pages.HomePage
import wandledi.scala.example.models.BlogEntry

class Home extends PageController {

  val page = new HomePage

  def getPage = page

  def index() {

    page.index(Array[BlogEntry]())

    val entry = BlogEntry.findAll.head
    entry.getComments
  }  
}
