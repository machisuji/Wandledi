package org.wandledi.wandlet.scala

import org.wandledi.Scroll
import org.wandledi.wandlet.Response
import org.wandledi.scala.SelectableImpl
import org.wandledi.Selector
import org.wandledi.PathSelector

class Page(val file: String) extends SelectableImpl(new Scroll) with Response {
  override def getFile = file

  /**
   * Extracts the target Element.
   *
   * @param sel Target Element to extract.
   * @param block Block to execute in the context of the extracted Element.
   */
  def extract(sel: Selector)(block: => Unit = {}) {
    get(new PathSelector).extract(sel)
    using(this at sel)(block)
  }

  override def toString = "scala.Page(" + getFile + ")"
}
