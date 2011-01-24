package wandledi.scala

import wandledi.scala.html.Page

abstract class PageController extends Controller with wandledi.java.SpellController {
  override def isSpellController = true
  def getPage: Page
}
