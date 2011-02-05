package org.wandledi.scala

import org.xml.sax.Attributes
import org.wandledi.Scroll
import org.wandledi.Selector
import org.wandledi.Spell
import org.wandledi.ElementForeachImpl
import org.wandledi.Plan
import org.wandledi.spells.InsertionIntent
import org.wandledi.spells.ReplacementIntent
import org.wandledi.spells.StringTransformation

class ElementImpl(
  aSelector: Selector,
  aScroll: Scroll)
extends org.wandledi.ElementImpl(aSelector, aScroll) with Element {

  private implicit def toJavaList[T: ClassManifest](l: Iterable[T]) =
    java.util.Arrays.asList(l.toArray: _*)

  override def foreachIn[T: ClassManifest](items: Iterable[T])(fun: (SelectableElement, T) => Unit) {
    val plan = new Plan[T] {
      def execute(e: org.wandledi.SelectableElement, item: T): Unit =
        fun(new SelectableElementImpl(e.getSelector, e.getScroll, e.getScroll), item)
    }
    val foreach = new ElementForeachImpl(this, items)
    foreach.apply(plan)
  }

  override def foreachWithIndexIn[T: ClassManifest](items: Iterable[T])
      (fun: (SelectableElement, T, Int) => Unit) {
    val plan = new Plan[T] {
      def execute(e: org.wandledi.SelectableElement, item: T): Unit =
        fun(new SelectableElementImpl(e.getSelector, e.getScroll, e.getScroll), item, index)
    }
    val foreach = new ElementForeachImpl(this, items)
    foreach.apply(plan)
  }

  override def changeAttribute(name: String)(change: (String) => String) {
    setAttribute(name, new StringTransformation {
      def transform(value: String) = change(value)
    })
  }

  override def includeFile(file: String)(magic: (Selectable) => Unit) {
    val scroll = new Scroll
    magic(new SelectableImpl(scroll))
    includeFile(file, scroll)
  }

  override def insert(atEnd: Boolean)(insertion: (Spell) => Unit) {
    val intent = new InsertionIntent {
      def insert(parent: Spell) {
        insertion(parent)
      }
    }
    insert(atEnd, intent)
  }

  override def replace(contentsOnly: Boolean)(replacement: (String, Attributes, Spell) => Unit) {
    val intent = new ReplacementIntent {
      def replace(label: String, attributes: Attributes, parent: Spell) {
        replacement(label, attributes, parent)
      }
    }
    replace(contentsOnly, intent)
  }
}
