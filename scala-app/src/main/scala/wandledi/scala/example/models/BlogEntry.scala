package wandledi.scala.example.models

import scala.reflect.BeanProperty
import wandledi.scala.Entity
import wandledi.scala.EntityCompanion
import wandledi.scala.Parameters

import javax.persistence.CascadeType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Lob
import javax.persistence.OneToMany
import javax.persistence.Temporal
import javax.persistence.TemporalType

import java.util.Date

//class BlogEntry extends JavaBlogEntry with Entity
/**
 *
 * @author Markus Kahl
 */
@javax.persistence.Entity
class BlogEntry(entryAuthor: String, entryTitle: String, entryContent: String) extends Entity {

  @Id
  @GeneratedValue
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var author: String = entryAuthor
  @BeanProperty
  var title: String = entryTitle

  @Lob
  @BeanProperty
  var content: String = entryContent

  @Temporal(TemporalType.TIMESTAMP)
  @BeanProperty
  var date: Date = new Date

  @OneToMany(cascade = Array(CascadeType.PERSIST, CascadeType.REMOVE))
  @BeanProperty
  var comments: java.util.Collection[Comment] = _

  def this() = this(null, null, null)

  def this(params: Parameters) = this(params("author"), params("title"), params("content"))

  def validate: Boolean = ok(author) && ok(title) && ok(content)

  def ok(field: String): Boolean = field != null && !field.isEmpty
}

object BlogEntry extends EntityCompanion[BlogEntry] {

  override def entityClass = classOf[BlogEntry]
}
