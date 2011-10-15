package org.wandledi.scala

import org.wandledi.{Selector => JSelector, UniversalSelector, Attribute, Scroll}
import scala.util.DynamicVariable

/**
 * A Selectable lets you select elements in a document so that they can be transformed.
 * Which elements will be selected depends on the used Selector.
 * No matter how many elements match (including 0), there will always be a single Element instance returned.
 * All Spells created through that Element will be applied to each matched HTML element separately.
 * If no elements were matched the created Spells will have no effect.
 */
trait Selectable extends org.wandledi.Selectable {

  protected type SelectContext = DynamicVariable[Selectable]
  protected val selectContext = new SelectContext(this)

  protected implicit def enclosingSelectable = this

  /**
   * Selects elements matched by a given Selector.
   *
   * @param selector Selector to be used to match the target element(s).
   * @return An Element providing means to transform the target element(s).
   */
  def get(selector: JSelector): Element

  /**
   * Selects elements with the specified attributes.
   *
   * @param atts Element attribute name-value pairs.
   * @return An Element providing means to transform the target element(s).
   */
  def get(atts: (String, String)*): Element

  /**
   * Selects elements with the specified attributes and tag name.
   *
   * @param name Target element name.
   * @param attrHead One of the target element's attributes.
   * @param attrTail The rest of the target element's attributes.
   *
   * @return An Element providing means to transform the target element(s).
   */
  def get(name: String, attrHead: (String, String), attrTail: (String, String)*): Element

  /**
   * Selects elements matching a given CSS selector.
   *
   * @param selector CSS selector to be used to match elements.
   * @return An Element providing means to transform the target element(s).
   * @see org.wandledi.CssSelector
   */
  def get(selector: String): Element

  /**
   * Selects another Selectable which can only select elements underneath it
   * For instance <em>at("head").get("body")</em> will match nothing, since the body element
   * is not a child of the head.
   *
   * @param selector Selector to be used to match the target element(s).
   * @return A SelectableElement providing transformations for the target element(s) as well
   *         as selection methods to select child elements.
   */
  def at(selector: JSelector): SelectableElement
  /**
   * Selects another Selectable which can only select elements underneath it
   * For instance <em>at("head").get("body")</em> will match nothing, since the body element
   * is not a child of the head.
   *
   * @param selector CSS selector to be used to match the target element(s).
   * @return A SelectableElement providing transformations for the target element(s) as well
   *         as selection methods to select child elements.
   * @see org.wandledi.CssSelector
   */
  def at(selector: String): SelectableElement

  /**
   * Selects elements using the current context,
   * which is - per default - this Selectable.
   * Context changes upon usage of <em>$$</em> or <em>#using</em>.
   *
   * @param selector Selector to be used to match the target element(s).
   *
   * @return An Element providing transformations.
   */
  def $(selector: JSelector) = selectContext.value.get(selector)

  /**
   * Selects elements with the specified attributes from the current context.
   *
   * @param atts attribute name-value pairs.
   *
   * @return An Element providing transformations.
   * @see org.wandledi.UniversalSelector
   */
  def $(atts: Tuple2[String, String]*) = selectContext.value.get(atts: _*)

  /**
   * Selects elements with the specified attributes and tag name from the current context.
   *
   * @param name Element tag name.
   * @param atts attribute name-value pairs.
   *
   * @return An Element providing transformations.
   * @see org.wandledi.UniversalSelector
   */
  def $(name: String, attrHead: (String, String), attrTail: (String, String)*) =
    selectContext.value.get(name, attrHead, attrTail: _*)

  /**
   * This refers to the current SelectableElement,
   * which is only defined inside a $$ block.
   *
   * If this is called outside such a block a RuntimeException will be thrown.
   */
  def $: SelectableElement = selectContext.value match {
    case e: SelectableElement => e
    case _ => throw new RuntimeException(
        "Current context is no SelectableElement. " +
        "Do not use $ outside $$ blocks."
      )
  }

  /**
   * Selects an Element and evaluates the given block with that element
   * as the new context. Example (inside the class body of another Selectable):
   *
   * <pre>$$(".footer") {
   *   $("a").changeAttribute("href", "/"+_) // only affects anchors inside the .footer element
   *   $.hide() // $ is the current context Element itself; only available inside $$ blocks
   * }
   * </pre>
   *
   * @param selector Selector to be used to match the target element(s).
   * @param block Block to be called with the target Element as context.
   */
  def $$(selector: JSelector)(block: => Unit) {
    val selectable = selectContext.value
    val selected = selectable.at(selector)
    selectContext.withValue(selected)(block)
  }

  /**
   * Selects an Element and evaluates the given block with that element
   * as the new context. Example (inside the class body of another Selectable):
   *
   * <pre>$$(".footer") {
   *   $("a").changeAttribute("href", "/"+_) // only affects anchors inside the .footer element
   *   $.hide() // $ is the current context Element itself; only available inside $$ blocks
   * }
   * </pre>
   *
   * @param name Target element's tag name.
   * @param attributesHead Element attribute key-value pair used for matching.
   * @param attributesTail Element attribute key-value pairs used for matching.
   * @param block Block to be called with the target Element as context.
   */
  def $$(name: String, attributesHead: (String, String),
         attributesTail: (String, String)*)(block: => Unit) {
    val attr = (attributesHead +: attributesTail).map(attr => new Attribute(attr._1, attr._2))
    $$(new UniversalSelector(name, attr: _*))(block)
  }

  /**
   * Selects an Element and evaluates the given block with that element
   * as the new context. Example (inside the class body of another Selectable):
   *
   * <pre>$$(".footer") {
   *   $("a").changeAttribute("href", "/"+_) // only affects anchors inside the .footer element
   *   $.hide() // $ is the current context Element itself; only available inside $$ blocks
   * }
   * </pre>
   *
   * @param attributes Element attribute name-value pairs to be used for matching.
   * @param block Block to be called with the target Element as context.
   *
   * @see org.wandledi.UniversalSelector
   */
  def $$(attributes: (String, String)*)(block: => Unit) {
    val attr = attributes.map(attr => new Attribute(attr._1, attr._2))
    $$(new UniversalSelector(null.asInstanceOf[String], attr: _*))(block)
  }

  /**
   * Switches context to the given Selectable so that
   * usages of $() will refer to that Selectable within a given block.
   *
   * @param e Selectable to be used as context.
   * @param block Block within which the new context is to used.
   */
  def using(e: Selectable)(block: => Unit) {
    selectContext.withValue(e)(block)
  }
}

object Selectable {
  /**
   * Creates a new Selectable for a given Scroll.
   * All Spells created through the Elements obtained through this Selectable will
   * be stored in that Scroll.
   *
   * @param scroll Scroll to be used by this Selectable.
   * @return A new Selectable to be used for selecting elements.
   */
  def apply(scroll: Scroll) = new SelectableImpl(scroll)

  def apply() = new SelectableImpl
}
