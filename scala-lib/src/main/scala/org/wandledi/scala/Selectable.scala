package org.wandledi.scala

import org.wandledi.{Selector, CssSelector, UniversalSelector, Attribute, Scroll}
import scala.util.DynamicVariable

trait Selectable extends org.wandledi.Selectable {

  protected type SelectContext = DynamicVariable[Selectable]
  protected val selectContext = new SelectContext(this)

  implicit val enclosingSelectable = this

  def get(selector: Selector): Element
  def get(atts: Tuple2[String, String]*): Element
  def get(label: String, atts: Tuple2[String, String]*): Element
  def get(selector: String): Element
  def at(selector: Selector): SelectableElement
  def at(selector: String): SelectableElement

  /**Gets an element from the current context,
   * which is - per default - this ScalaSelectable.
   * Context changes upon usage of $$.
   */
  def $(selector: Selector) = selectContext.value.get(selector)
  def $(atts: Tuple2[String, String]*) = selectContext.value.get(atts: _*)
  def $(label: String, atts: Tuple2[String, String]*) = selectContext.value.get(label, atts: _*)
  def $(selector: String) = selectContext.value.get(selector)

  /**This refers to the current SelectableElement,
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

  /**Selects an Element and evaluates the given block with that element
   * as its context. Example:
   *
   * <pre>val body = $("body")
   *
   */
  def $$(selector: Selector)(block: => Unit) {
    val selectable = selectContext.value
    val selected = selectable.at(selector)
    selectContext.withValue(selected)(block)
  }

  def $$(label: String, attributesHead: (String, String),
         attributesRest: (String, String)*)(block: => Unit) {
    val attr = (attributesHead +: attributesRest).map(attr => new Attribute(attr._1, attr._2))
    $$(new UniversalSelector(label, attr: _*))(block)
  }

  def $$(attributes: (String, String)*)(block: => Unit) {
    val attr = attributes.map(attr => new Attribute(attr._1, attr._2))
    $$(new UniversalSelector(null.asInstanceOf[String], attr: _*))(block)
  }

  /**Switches context to the given Selectable so that
   * following usages of $() will refer to that Selectable.
   */
  def using(e: Selectable)(block: => Unit) {
    selectContext.withValue(e)(block)
  }

  /**Enables strings as selectors by implicitly converting them
   * using CssSelector.
   */
  implicit def cssSelector(selector: String): Selector = CssSelector.valueOf(selector)
}

object Selectable {
  def apply(scroll: Scroll) = new SelectableImpl(scroll)
}
