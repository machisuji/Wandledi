package wandledi.scala.html

import wandledi.core.Scroll
import wandledi.java.Messages
import wandledi.java.html.PageImpl

class Page(
  private val jpage: PageImpl,
  private var messages: Messages,
  private var file: String
) extends Selectable(new Scroll) with wandledi.java.html.Page {

  def this() {
    this(new PageImpl, null, null)
  }

  def msg(key: Object, args: Object*) = jpage.msg(key, args: _*)

  override def setMessages(messages: Messages) {
    this.messages = messages
  }

  override def getMessages = messages

  override def setFile(file: String) {
    this.file = file
  }

  override def getFile = file
}
