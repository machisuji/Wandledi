package wandledi.scala.html

import wandledi.java.html.SelectableElement
import org.xml.sax.Attributes
import wandledi.core.Scroll
import wandledi.core.Selector
import wandledi.core.SimpleAttributes
import wandledi.core.Spell
import wandledi.java.html.ElementForeachImpl
import wandledi.java.html.ElementImpl
import wandledi.java.html.Plan
import wandledi.spells.InsertionIntent
import wandledi.spells.ReplacementIntent
import wandledi.spells.StringTransformation

class Element(selector: Selector, scroll: Scroll) extends wandledi.java.html.ElementImpl(selector, scroll) {

  private implicit def toJavaList[T: ClassManifest](l: Seq[T]) =
    java.util.Arrays.asList(l.toArray: _*)

  def foreachIn[T: ClassManifest](items: Seq[T])(fun: (SelectableElement, T) => Unit) {
    val plan = new Plan[T] {
      def execute(e: SelectableElement, item: T): Unit = fun(e, item)
    }
    val foreach = new ElementForeachImpl(this, items)
    foreach.apply(plan)
  }

  def foreachWithIndexIn[T: ClassManifest](items: Seq[T])(fun: (SelectableElement, T, Int) => Unit) {
    val plan = new Plan[T] {
      def execute(e: SelectableElement, item: T): Unit = fun(e, item, index)
    }
    val foreach = new ElementForeachImpl(this, items)
    foreach.apply(plan)
  }

  def changeAttribute(name: String)(change: (String) => String) {
    setAttribute(name, new StringTransformation {
      def transform(value: String) = change(value)
    })
  }

  def includeFile(file: String)(magic: (Scroll) => Unit) {
    val scroll = new Scroll
    magic(scroll)
    includeFile(file, scroll)
  }

  def insert(atEnd: Boolean)(insertion: (Spell) => Unit) {
    val intent = new InsertionIntent {
      def insert(parent: Spell) {
        insertion(parent)
      }
    }
    insert(atEnd, intent)
  }

  // @TODO #insert which simply takes a function returning an XML node to be written

  def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit) {
    val intent = new ReplacementIntent {
      def replace(label: String, attributes: Attributes, parent: Spell) {
        replacement(label, attributes, parent)
      }
    }
    replace(contentsOnly, intent)
  }

  // @TODO #replace which simply takes a function returning an XML node to be written

  def test {

    changeAttribute("class") {
      ".entry " + _
    }
    changeAttribute("class")(".entry " + _)
    setAttribute("style", "color: red;")
    foreachWithIndexIn(List(1, 2, 3)) { (e, item, index) =>
      e.replace(true, item.toString + " in row " + (index + 1))
    }
    includeFile("/home/post.xhtml") { (page) =>
      page.get("#right").insert("message")
    }
    insert(true) { (spell) =>
      spell.writeString("Hallo Welt")
    }
    replace(true) { (label, attributes, spell) =>
      spell.startElement("p", new SimpleAttributes)
      spell.writeString("Paragraph")
      spell.endElement("p")
    }
  }
}
