package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

trait Element extends org.wandledi.Element {

  def foreachIn[T: ClassManifest](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T) => Unit): Unit
  def foreachWithIndexIn[T: ClassManifest](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T, Int) => Unit): Unit

  def changeAttribute(name: String, change: (String) => String): Unit
  def changeAttributes(attr: (String, (String) => String)*): Unit
  def setAttributes(attr: (String, String)*): Unit

  def includeFile(file: String)(magic: (Selectable) => Unit): Unit

  def insert(atEnd: Boolean)(insertion: (Spell) => Unit): Unit
  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) with the XML returned by the given function.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacement A function which is passed name and attributes of this element and is to return
   *                    XML for the replacement.
   */
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes) => xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) with XML.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacement XML for the replacement.
   */
  def replace(contentsOnly: Boolean, replacement: xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) according to the given intent.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacementIntent A function which is passed name and attributes of this element, as well as a Spell
   *                          to be used for writing new content with help of its methods #startElement,
   *                          #endElement and #writeCharacters.
   * @see org.wandledi.spells.ReplacementIntent
   */
  def replace(contentsOnly: Boolean, replacementIntent: (String, Attributes, Spell) => Unit)

  /**
   * Sets this Element's text. Equivalent to Element.text.setContent().
   */
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
