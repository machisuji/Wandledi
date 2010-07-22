package wandledi.test

import wandledi.scala.Entity
import wandledi.scala.EntityCompanion

class User(
  var name: String,
  var email: String,
  var age: Int
) extends Entity

object User extends EntityCompanion[User] {

  override def entityClass = classOf[User]
}
