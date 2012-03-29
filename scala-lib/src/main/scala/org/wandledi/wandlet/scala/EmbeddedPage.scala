package org.wandledi.wandlet.scala

import org.wandledi.CssSelector
import org.wandledi.Selector

class EmbeddedPage(
                    file: String,
                    hostFile: String,
                    embedAt: Selector
                    ) extends Page(null) {
  val gantry = new Page(hostFile)
  gantry.get(embedAt).includeFile(file, scroll)

  def this(file: String, hostFile: String, embedAt: String) =
    this(file, hostFile, CssSelector.valueOf(embedAt))

  override def getFile = gantry.getFile

  override def getScroll = gantry.getScroll
}
