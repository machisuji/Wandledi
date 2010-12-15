package wandledi.scala

import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._

class RequestData(request: HttpServletRequest) extends scala.collection.mutable.Map[String, Any] {
  
  def get(key: String): Option[Any] = {
    val value = request.getAttribute(key)
    if (value != null) Some(value) else None
  }

  def getAs[T](key: String): Option[T] = get(key).map(_.asInstanceOf[T])

  override def +[B1 >: Any](kv: (String, B1)): RequestData = {
    request.setAttribute(kv._1, kv._2)
    this
  }

  def +=(kv: (String, Any)) = this.+(kv).asInstanceOf[RequestData.this.type]

  override def -(key: String): RequestData = {
    request.removeAttribute(key)
    this
  }

  def -=(key: String) = this.-(key).asInstanceOf[RequestData.this.type]

  override def empty: RequestData = new RequestData(request)

  def iterator: Iterator[(String, Any)] = {
    request.getAttributeNames().map { attr =>
      val name = attr.toString
      (name, request.getAttribute(name))
    }
  }

  override def foreach[U](fun: ((String, Any)) => U) = iterator.foreach(fun(_))
}
