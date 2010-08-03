package wandledi.scala.example.models

import wandledi.scala.Entity
import wandledi.scala.EntityCompanion

class BlogEntry extends JavaBlogEntry with Entity

object BlogEntry extends EntityCompanion[BlogEntry] {

  override def entityClass = classOf[BlogEntry]
}
