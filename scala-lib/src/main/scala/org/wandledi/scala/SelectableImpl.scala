package org.wandledi.scala

import org.wandledi.Attribute
import org.wandledi.CssSelector
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.UniversalSelector
import org.wandledi.LocalSpells

class SelectableImpl(aScroll: Scroll) extends org.wandledi.SelectableImpl(aScroll) with Selectable {

  def this() = this(new Scroll)

  override def get(selector: Selector) = new ElementImpl(selector, scroll)

  def get(atts: (String, String)*): Element = {
    val jatts = for (attr <- atts) yield new Attribute(attr._1, attr._2)
    get(new UniversalSelector(jatts: _*))
  }

  def get(label: String, attrHead: (String, String), attrTail: Tuple2[String, String]*): Element = {
    val jatts = for (attr <- (attrHead +: attrTail)) yield new Attribute(attr._1, attr._2)
    get(new UniversalSelector(label, jatts: _*))
  }

  override def get(selector: String): Element = get(CssSelector.valueOf(selector))

  override def at(selector: Selector) = {
    val nestedScroll = new Scroll
    val localSpell = new LocalSpells(scroll, nestedScroll)
    scroll.addSpell(selector, localSpell)
    new SelectableElementImpl(selector, scroll, nestedScroll)
  }

  override def at(selector: String) = at(CssSelector.valueOf(selector))
}
