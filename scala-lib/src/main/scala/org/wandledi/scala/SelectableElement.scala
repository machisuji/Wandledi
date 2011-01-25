package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

class SelectableElement(
  selector: Selector, 
  scroll: Scroll)
extends org.wandledi.SelectableElement(selector, scroll) with ScalaSelectable with ScalaElement {

  private val selectable = new Selectable(scroll)

  override def get(selector: Selector): Element = selectable.get(selector)
  override def get(atts: Tuple2[String, String]*): Element = selectable.get(atts: _*)
  override def get(label: String, atts: Tuple2[String, String]*): Element = selectable.get(label, atts: _*)
  override def get(selector: String): Element = selectable.get(selector)
  override def at(selector: Selector): Selectable = selectable.at(selector)
  override def at(selector: String): Selectable = selectable.at(selector)

  private val element = new Element(selector, scroll)

  override def foreachIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T) => Unit) =
    element.foreachIn(items)(fun)
  override def foreachWithIndexIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T, Int) => Unit) =
    element.foreachWithIndexIn(items)(fun)
  override def changeAttribute(name: String)(change: (String) => String) =
    element.changeAttribute(name)(change)
  override def includeFile(file: String)(magic: (Selectable) => Unit) =
    element.includeFile(file)(magic)
  // @TODO #insert which simply takes a function returning an XML node to be written
  override def insert(atEnd: Boolean)(insertion: (Spell) => Unit) =
    element.insert(atEnd)(insertion)
  // @TODO #replace which simply takes a function returning an XML node to be written
  override def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit) =
    element.replace(contentsOnly)(replacement)
}
