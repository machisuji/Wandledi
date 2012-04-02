package wandledi.scala.tests

import org.wandledi.scala._
import org.wandledi.{CssSelector, Scroll, SimpleAttributes}

/**This is not a real test.
 * It merely has to compile.
 */
class SyntaxTest {
  val e = Element(new CssSelector("foo"), new Scroll)

  e.changeAttribute("class", {
    ".entry " + _
  })
  e.changeAttribute("class", "entry " + _)
  e.setAttribute("style", "color: red;")
  e.foreachWithIndexIn(List(1, 2, 3)) { (e, item, index) =>
    e.replace(true, item.toString + " in row " + (index + 1))
  }

  e.foreachIn(1 to 10 toList)((e, item) =>
    e.hide)
  e.foreachIn(1 to 10 toList, reduceBefore = true)((e, item) =>
    e.hide)

  e.insert(true) { (spell) =>
    spell.writeString("Hallo Welt", true)
  }
  e.replace(true, (label, attributes, spell) => {
    spell.startElement("p", new SimpleAttributes)
    spell.writeString("Paragraph", true)
    spell.endElement("p")
  })
  e.replace(true, <h1>Hallo Welt</h1>)
  e.replace(false) { (label, attr) =>
    <label>{label}</label>
  }
}

class Page extends SelectableImpl(new Scroll)

class HomePage extends Page with Module {
  def index() {
    $("body").setAttribute("style", "background-color: white;")
    $$(".title") {
      $.insertLast("foobar")
      $("h1").changeAttribute("class", "header " + _)
    }
    $$("class" -> "header") { }
    insertName("Hans Wurst")
  }
}

trait Module extends Selectable {
  def insertName(name: String) {
    $(".name").text = name
  }
}
