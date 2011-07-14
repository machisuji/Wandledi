package org.wandledi.scala

import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.PathSelector
import org.wandledi.{Element => JElement}

/**Paragraph - "A distinct portion of written or printed matter dealing with a particular idea[...]."
 *
 * To be used as a container for 'Scroll paragraphs', that is methods that perform
 * transformations using arbitrary Selectables. So that transformations
 * can be put into modules.
 *
 * Example:
 *
 * object CommonStuff extends Paragraphs {
 *   def insertName(name: String)(implicit context: Selectable) = using(context) {
 *     $(".name").text = name
 *   }
 * }
 *
 * All methods must have the same implicit parameter context and start with 'using(context) {'.
 * Such a module can then be used inside other Selectables like this:
 *
 * class Main(scroll: Scroll) extends SelectableImpl(scroll) {
 *   $("#title").insert("Hallo Welt!")
 *   Index.insertName("Hans Wurst") // Selectable provide an implicit 'this', which is the context here
 * }
 *
 */
trait Paragraphs extends Selectable {

  val error = """Error:
  This Selectable is only a container and always requires an explicit context which has to be set
  with #using. Example:
    def insertName(name: String)(implicit context: Selectable) = using(context) {
      $(".name").text = name
    }"""

  def notice = throw new UnsupportedOperationException(error)

  def get(selector: Selector): Element = notice
  def get(atts: Tuple2[String, String]*): Element = notice
  def get(label: String, atts: Tuple2[String, String]*): Element = notice
  def get(selector: String): Element = notice
  def at(selector: Selector): SelectableElement = notice
  def at(selector: String): SelectableElement = notice

  def getScroll: Scroll = notice
  def get(attr: org.wandledi.Attribute*): JElement = notice
  def get(label: String, attr: org.wandledi.Attribute*): JElement = notice
  def get(attr: String, value: String): JElement = notice
  def get(label: String, attr: String, value: String): JElement = notice
}
