package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

trait Element extends org.wandledi.Element {

  def foreachIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T) => Unit): Unit
  def foreachWithIndexIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T, Int) => Unit): Unit
  def changeAttribute(name: String)(change: (String) => String): Unit
  def includeFile(file: String)(magic: (Selectable) => Unit): Unit
  
  def insert(atEnd: Boolean)(insertion: (Spell) => Unit): Unit
  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq): Unit

  def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit): Unit
  def replace(contentsOnly: Boolean, replacement: (String, Attributes) => xml.NodeSeq): Unit
  def replace(contentsOnly: Boolean = true, replacement: xml.NodeSeq): Unit

  def text_=(value: String): Unit
  def text: TextContent

  /**Through this you can pass a block to a SelectableElement
   * within which it can be processed.
   * For example:
   *
   * $(".msg") { msg =>
   *   // transform msg
   * }
   */
  def apply(block: (Element) => Unit) = block(this)
}

object Element {
  def apply(selector: Selector, scroll: Scroll) = new ElementImpl(selector, scroll)
}
