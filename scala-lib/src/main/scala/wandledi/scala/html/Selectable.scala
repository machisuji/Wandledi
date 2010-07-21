package wandledi.scala.html

import wandledi.core.Attribute
import wandledi.core.CssSelector
import wandledi.core.Scroll
import wandledi.core.Selector
import wandledi.core.UniversalSelector
import wandledi.java.html.LocalSpells

class Selectable(scroll: Scroll) extends wandledi.java.html.SelectableImpl(scroll) {

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

  override def at(selector: Selector): Selectable = {
    val nestedScroll = new Scroll
    val localSpell = new LocalSpells(scroll, nestedScroll)
    scroll.addSpell(selector, localSpell)
    new Selectable(nestedScroll)
  }

  override def at(selector: String): Selectable = at(CssSelector.valueOf(selector))
}
