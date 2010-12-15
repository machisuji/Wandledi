package wandledi.scala

/**Encapsulates HTTP GET parameters of a HttpServletRequest.
 */
import javax.servlet.http.HttpServletRequest
import wandledi.java.RequestWrapper
import collection.JavaConversions._

class Parameters(request: HttpServletRequest) extends scala.collection.mutable.Map[String, String] {

  def get(key: String): Option[String] = {
    val value = request.getParameter(key)
    if (value != null) Some(value) else None
  }

  def +(kv: (String, String)): Parameters = {
    request.asInstanceOf[RequestWrapper].setParameter(kv._1, kv._2.toString)
    this
  }

  def +=(kv: (String, String)) = this.+(kv).asInstanceOf[Parameters.this.type]

  override def -(key: String): Parameters = {
    this.+(key -> null)
    this
  }

  def -=(key: String) = this.-(key).asInstanceOf[Parameters.this.type]

  override def empty: Parameters = new Parameters(request)

  def iterator: Iterator[(String, String)] = request.getParameterNames().map { name =>
    val param = name.toString
    (param, request.getParameter(param))
  }

  override def size: Int = request.getParameterMap().size()

  override def foreach[U](fun: ((String, String)) => U) = iterator.foreach(fun(_))
}
