package org.wandledi.wandlet.scala

import org.wandledi.Scroll
import org.wandledi.wandlet.Response
import org.wandledi.scala.Selectable

class Page(val file: String) extends Selectable(new Scroll) with Response {
    override def getFile = file
}
