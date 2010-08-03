package wandledi.scala.example.models

import wandledi.scala.Entity
import wandledi.scala.EntityCompanion

class Comment extends JavaComment with Entity

object Comment extends EntityCompanion[Comment] {

  override def entityClass = classOf[Comment]
}
