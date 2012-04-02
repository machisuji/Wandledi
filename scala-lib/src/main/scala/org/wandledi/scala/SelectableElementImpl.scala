package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

class SelectableElementImpl(
  selector: Selector,
  parentScroll: Scroll,
  localScroll: Scroll
) extends org.wandledi.SelectableElementImpl(selector, parentScroll, localScroll) with SelectableElement {

  private val selectable = new SelectableImpl(localScroll)

  override def get(selector: Selector): Element = selectable.get(selector)
  override def get(atts: Tuple2[String, String]*): Element = selectable.get(atts: _*)
  override def get(label: String, attrHead: (String, String), attrTail: (String, String)*): Element =
    selectable.get(label, attrHead, attrTail: _*)
  override def get(selector: String): Element = selectable.get(selector)
  override def at(selector: Selector) = selectable.at(selector)
  override def at(selector: String) = selectable.at(selector)

  private val element = new ElementImpl(selector, parentScroll)

  def foreachIn[T](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T) => Unit)
    (implicit context: Selectable = null) = element.foreachIn(items, reduceBefore)(fun)(context)
  def foreachWithIndexIn[T](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T, Int) => Unit)
    (implicit context: Selectable = null) = element.foreachWithIndexIn(items, reduceBefore)(fun)(context)

  def changeAttribute(name: String, change: (String) => String) =
    element.changeAttribute(name, change)
  def changeAttributes(attr: (String, (String) => String)*) =
    element.changeAttributes(attr: _*)
  def setAttributes(attr: (String, String)*) =
    element.setAttributes(attr: _*)

  def includeFile(file: String)(magic: => Unit)(implicit context: Selectable) =
    element.includeFile(file)(magic)(context)

  def insert(atEnd: Boolean)(insertion: (Spell) => Unit) =
    element.insert(atEnd)(insertion)
  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq) =
    element.insert(atEnd, insertion)

  def replace(contentsOnly: Boolean, replacement: (String, Attributes, Spell) => Unit) =
    element.replace(contentsOnly, replacement)
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes) => xml.NodeSeq) =
    element.replace(contentsOnly)(replacement)
  def replace(contentsOnly: Boolean, replacement: xml.NodeSeq) =
    element.replace(contentsOnly, replacement)

  def text_=(value: String) = element.text_=(value)
  def text: TextContent = element.text
}
