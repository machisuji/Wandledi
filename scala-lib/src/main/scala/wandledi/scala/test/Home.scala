package wandledi.test

import wandledi.scala.Controller

class Home extends Controller {

  def index() {
    val user = User.findAll.head
    user.name = "Heinz 2"
    user.save
  }
}
