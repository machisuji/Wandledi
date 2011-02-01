package wandledi.scala.tests

import org.wandledi.scala._
import org.wandledi.{CssSelector, Scroll, SimpleAttributes}

/**This is not a real test.
 * It merely has to compile.
 */
class SyntaxTest {
  val e = new Element(new CssSelector("foo"), new Scroll)

  e.changeAttribute("class") {
    ".entry " + _
  }
  e.changeAttribute("class")("entry " + _)
  e.setAttribute("style", "color: red;")
  e.foreachWithIndexIn(List(1, 2, 3)) { (e, item, index) =>
    e.replace(true, item.toString + " in row " + (index + 1))
  }
  e.includeFile("/home/post.xhtml") { (page) =>
    page.get("#right").insert("message")
  }
  e.insert(true) { (spell) =>
    spell.writeString("Hallo Welt")
  }
  e.replace(true) { (label, attributes, spell) =>
    spell.startElement("p", new SimpleAttributes)
    spell.writeString("Paragraph")
    spell.endElement("p")
  }
}

class Page extends Selectable(new Scroll)

class HomePage extends Page {
  def index() {
    $("body").setAttribute("style", "background-color: white;")
    $$(".title") {
      $.insertLast("foobar")
      $("h1").changeAttribute("class")("header " + _)
    }
    $$("class" -> "header") { }
  }
}