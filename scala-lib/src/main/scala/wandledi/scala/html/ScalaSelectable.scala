package wandledi.scala.html

import wandledi.core.Selector

trait ScalaSelectable {

  def get(selector: Selector): Element
  def get(atts: Tuple2[String, String]*): Element
  def get(label: String, atts: Tuple2[String, String]*): Element
  def get(selector: String): Element
  def at(selector: Selector): Selectable
  def at(selector: String): Selectable
}
