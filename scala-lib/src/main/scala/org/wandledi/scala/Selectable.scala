package org.wandledi.scala

import org.wandledi.Attribute
import org.wandledi.CssSelector
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.UniversalSelector
import org.wandledi.LocalSpells

class Selectable(aScroll: Scroll) extends org.wandledi.SelectableImpl(aScroll) with ScalaSelectable {

  override def get(selector: Selector) = new Element(selector, scroll)

  def get(atts: Tuple2[String, String]*): Element = {
    val jatts = for (attr <- atts) yield new Attribute(attr._1, attr._2)
    get(new UniversalSelector(jatts: _*))
  }

  def get(label: String, atts: Tuple2[String, String]*): Element = {
    val jatts = for (attr <- atts) yield new Attribute(attr._1, attr._2)
    get(new UniversalSelector(label, jatts: _*))
  }

  override def get(selector: String): Element = get(CssSelector.valueOf(selector))

  override def at(selector: Selector) = {
    val nestedScroll = new Scroll
    val localSpell = new LocalSpells(scroll, nestedScroll)
    scroll.addSpell(selector, localSpell)
    new SelectableElement(selector, nestedScroll)
  }

  override def at(selector: String) = at(CssSelector.valueOf(selector))
}
