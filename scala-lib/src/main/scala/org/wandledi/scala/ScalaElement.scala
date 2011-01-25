package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Spell

trait ScalaElement {

  def foreachIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T) => Unit): Unit
  def foreachWithIndexIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T, Int) => Unit): Unit
  def changeAttribute(name: String)(change: (String) => String): Unit
  def includeFile(file: String)(magic: (Selectable) => Unit): Unit
  // @TODO #insert which simply takes a function returning an XML node to be written
  def insert(atEnd: Boolean)(insertion: (Spell) => Unit): Unit
  // @TODO #replace which simply takes a function returning an XML node to be written
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit): Unit
}
