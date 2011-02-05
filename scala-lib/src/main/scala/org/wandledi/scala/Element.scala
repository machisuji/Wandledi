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
  // @TODO #insert which simply takes a function returning an XML node to be written
  def insert(atEnd: Boolean)(insertion: (Spell) => Unit): Unit
  // @TODO #replace which simply takes a function returning an XML node to be written
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit): Unit

  /* def text_=(value: String): Unit = ... // directly change whole contained text
   * def text: TransformableString = ...
   *
   * text.insert(user.name, user.age, user.height)
   * text.insert('name -> user.name, 'age -> user.age, 'height -> user.height)
   * text.insert(1 -> user.age, 0 -> user.name, 2 -> user.height)
   * text.replaceAll("\\(Markus\\)", user.name)
   */
}

object Element {
  def apply(selector: Selector, scroll: Scroll) = new ElementImpl(selector, scroll)
}
