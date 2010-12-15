package wandledi.scala

import collection.JavaConversions._

class Flash(flash: wandledi.java.Flash) extends scala.collection.mutable.Map[String, Any] {

  def get(key: String): Option[Any] = {
    val value = flash.get(key)
    if (value != null) Some(value) else None
  }

  def getAs[T](key: String): Option[T] = get(key).map(_.asInstanceOf[T])

  override def +[B1 >: Any](kv: (String, B1)): Flash = {
    flash.put(kv._1, kv._2)
    this
  }

  def +=(kv: (String, Any)) = this.+(kv).asInstanceOf[Flash.this.type]

  override def -(key: String): Flash = {
    flash.remove(key)
    this
  }

  def -=(key: String) = this.-(key).asInstanceOf[Flash.this.type]

  override def empty: Flash = new Flash(new wandledi.java.Flash)

  def iterator: Iterator[(String, Any)] = (flash.keySet.map { key =>
    (key.toString, flash.get(key).asInstanceOf[Any])
  }).iterator

  override def size: Int = flash.size

  override def foreach[U](f: ((String, Any)) => U) {
    flash.keySet.foreach { key =>
      f((key, flash.get(key).asInstanceOf[Any]))
    }
  }
}
