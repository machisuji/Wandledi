package wandledi.scala.example.models

import scala.reflect.BeanProperty
import wandledi.scala.Entity
import wandledi.scala.EntityCompanion
import wandledi.scala.Parameters
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.Temporal
import javax.persistence.TemporalType

@javax.persistence.Entity
class Comment(commentAuthor: String, commentEmail: String, commentContent: String) extends Entity {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var author: String = commentAuthor
  @BeanProperty
  var email: String = commentEmail

  @Lob
  @BeanProperty
  var content: String = commentContent

  @Temporal(TemporalType.TIMESTAMP)
  @BeanProperty
  var date: java.util.Date = new java.util.Date

  def this() = this(null, null, null)

  def this(params: Parameters) = this(params("author"), params("email"), params("content"))

  def validate: Boolean = ok(author) && (!ok(email) || email.contains("@")) && ok(content)

  def ok(field: String): Boolean = field != null && !field.isEmpty
}

object Comment extends EntityCompanion[Comment] {

  override def entityClass = classOf[Comment]
}
