package org.wandledi.wandlet.scala

import org.wandledi.Scroll
import org.wandledi.wandlet.Response
import org.wandledi.scala.SelectableImpl

class Page(val file: String) extends SelectableImpl(new Scroll) with Response {
    override def getFile = file
}
