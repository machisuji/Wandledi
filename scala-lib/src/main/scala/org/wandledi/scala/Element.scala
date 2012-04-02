package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell

/**
 * Obtained via a Selectable an Element is the main interface for the user to perform transformations.
 * While all transformations are defined as classes (Spells) and can also be applied directly
 * via Element#cast(Spell), this trait makes them usable more comfortably by providing
 * various methods adding Spells to an Element's Scroll.
 */
trait Element extends org.wandledi.Element {

  /**
   * Replicates this Element for each item in a given collection and provides the possiblity
   * to apply modifications during each replication.
   *
   * @param items The items to be used for the modifications. The target Element gets replicated once for each item.
   * @param reduceBefore If true, all matched elements are reduced to one, which then gets replicated.
   *                     If false each matched element will be replicated.
   * @param fun A function taking a SelectableElement and an item from the collection. The SelectableElement is the
   *            currently replicated Element and allows to apply modifications to it.
   * @param context The Selectable in the context of which this is called. If provided, that Selectable's
   *                selection context will be set to each respective SelectableElement during the duplication.
   *
   * @see org.wandledi.spells.Duplication
   * @see org.wandledi.spells.Changeling
   * @see org.wandledi.ElementForeach
   */
  def foreachIn[T](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T) => Unit)(implicit context: Selectable = null): Unit
  /**
   * Replicates this Element for each item in a given collection and provides the possiblity
   * to apply modifications during each replication.
   *
   * @param items The items to be used for the modifications. The target Element gets replicated once for each item.
   * @param reduceBefore If true, all matched elements are reduced to one, which then gets replicated.
   *                     If false each matched element will be replicated.
   * @param fun A function taking a SelectableElement, an item from the collection and the index of that item within
   *            the collection. The SelectableElement is the currently replicated Element and allows to apply modifications to it.
   * @param context The Selectable in the context of which this is called. If provided, that Selectable's
   *                selection context will be set to each respective SelectableElement during the duplication.
   *
   * @see org.wandledi.spells.Duplication
   * @see org.wandledi.spells.Changeling
   * @see org.wandledi.ElementForeach
   */
  def foreachWithIndexIn[T](items: Iterable[T], reduceBefore: Boolean = false)
    (fun: (SelectableElement, T, Int) => Unit)(implicit context: Selectable = null): Unit

  /**
   * Changes an existing attribute of this Element. It doesn't have any effect if there is no such attribute.
   *
   * @param name Name of the attribute to be changed.
   * @param change Function taking the original attribute's value and returning its new one.
   *
   * @see org.wandledi.spells.AttributeTransformation
   */
  def changeAttribute(name: String, change: (String) => String): Unit

  /**
   * Changes several existing attributes of this Element. Given transformations for attributes that don't exist
   * will have no effect.
   *
   * @param attr 2-Tuples associating transformation functions to attribute names.
   *             The transformation functions take an attribute's original value and return a new one.
   *
   * @see org.wandledi.spells.AttributeTransformation
   */
  def changeAttributes(attr: (String, (String) => String)*): Unit

  /**
   * Sets several attributes of this Element. Attributes that already exist will be overwritten.
   *
   * @param attr 2-Tuples with the first elements being the attribute names and the second elements being their
   *             respective values.
   *
   * @see org.wandledi.spells.AttributeTransformation
   */
  def setAttributes(attr: (String, String)*): Unit

  /**
   * Includes the given file in the place of this Element during which this Element is consumed.
   *
   * @param file The HTML file to be included.
   * @param magic A function for performing transformations on the included markup.
   * @param context The Selectable to be used for the magic.
   *
   * @see org.wandledi.spells.Inclusion
   */
  def includeFile(file: String)(magic: => Unit)(implicit context: Selectable): Unit

  /**
   * Inserts XML into this Element.
   *
   * @param atEnd If true the new content is inserted after this Element's last child node (incl. text).
   *              Otherwise it will be inserted right at the beginning, before its original content.
   * @param insertion A function taking a Spell which can be used to insert new HTML elements.
   *
   * @see org.wandledi.spell.Insertion
   */
  def insert(atEnd: Boolean)(insertion: (Spell) => Unit): Unit

  /**
   * Inserts XML into this Element.
   *
   * @param atEnd If true the new content is inserted after this Element's last child node (incl. text).
   *              Otherwise it will be inserted right at the beginning, before its original content.
   * @param insertion XML which will be inserted as plain, though unescaped text.
   *                  That is Spells besides TextTransformation don't apply.
   *
   * @see org.wandledi.spells.Insertion
   */
  def insert(atEnd: Boolean = false, insertion: xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) with the XML returned by the given function.
   * Note that the XML will be inserted as plain, though unescaped text.
   * That is Spells besides TextTransformation don't apply.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacement A function which is passed name and attributes of this element and is to return
   *                    XML for the replacement.
   *
   * @see org.wandledi.spells.Replacement
   */
  def replace(contentsOnly: Boolean)(replacement: (String, Attributes) => xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) with XML.
   * Note that the XML will be inserted as plain, though unescaped text.
   * That is Spells besides TextTransformation don't apply.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacement XML for the replacement.
   *
   * @see org.wandledi.spells.Replacement
   */
  def replace(contentsOnly: Boolean, replacement: xml.NodeSeq): Unit

  /**
   * Replace this element (or its contents only) according to the given intent.
   *
   * @param contentsOnly Replace only the element's contents instead of the whole element?
   * @param replacementIntent A function which is passed name and attributes of this element, as well as a Spell
   *                          to be used for writing new content with help of its methods #startElement,
   *                          #endElement and #writeCharacters.
   *
   * @see org.wandledi.spells.Replacement
   */
  def replace(contentsOnly: Boolean, replacementIntent: (String, Attributes, Spell) => Unit)

  /**
   * Sets this Element's text. Equivalent to Element.text.setContent().
   *
   * @see org.wandledi.spells.TextTransformation
   */
  def text_=(value: String): Unit

  /**
   * TextContent used to transform this Element's text.
   *
   * @see org.wandledi.spells.TextTransformation
   */
  def text: TextContent

  /**
   * Through this you can pass a block to a SelectableElement
   * within which it can be processed.
   * For example:
   *
   * <pre>$(".msg") { msg =>
   *   // transform msg
   * }</pre>
   */
  def apply(block: (Element) => Unit) = block(this)
}

object Element {
  /**
   * Creates a new Element, the Spells for which are to be stored in a given Scroll.
   *
   * @param selector The Selector matching this Element's pendant in the HTML document.
   * @param scroll The Scroll in which Spells created for this Element are to be stored.
   */
  def apply(selector: Selector, scroll: Scroll) = new ElementImpl(selector, scroll)
}
