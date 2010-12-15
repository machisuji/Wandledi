package wandledi.scala

import javax.servlet.http.HttpSession
import collection.JavaConversions._

class SessionData(session: HttpSession) extends scala.collection.mutable.Map[String, Any] {

  def get(key: String): Option[Any] = {
    val value = session.getAttribute(key)
    if (value != null) Some(value) else None
  }

  def getAs[T](key: String): Option[T] = get(key).map(_.asInstanceOf[T])

  override def +[B1 >: Any](kv: (String, B1)): SessionData = {
    session.setAttribute(kv._1, kv._2)
    this
  }

  def +=(kv: (String, Any)) = this.+(kv).asInstanceOf[SessionData.this.type]

  override def -(key: String): SessionData = {
    session.removeAttribute(key)
    this
  }

  def -=(key: String) = this.-(key).asInstanceOf[SessionData.this.type]

  override def empty: SessionData = new SessionData(session)

  def iterator: Iterator[(String, Any)] = {
    session.getAttributeNames().map { attr =>
      val name = attr.toString
      (name, session.getAttribute(name))
    }
  }

  override def foreach[U](fun: ((String, Any)) => U) = iterator.foreach(fun(_))
}
