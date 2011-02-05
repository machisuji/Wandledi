package org.wandledi.scala

import org.wandledi.Scroll
import org.wandledi.Selector

trait SelectableElement extends org.wandledi.SelectableElement with Element with Selectable

object SelectableElement {
  def apply(selector: Selector, parentScroll: Scroll, localScroll: Scroll) =
    new SelectableElementImpl(selector, parentScroll, localScroll)
}
