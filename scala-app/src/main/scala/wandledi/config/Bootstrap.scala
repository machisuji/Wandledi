package wandledi.config

import wandledi.java.WandlediBootstrap
import javax.servlet.ServletContext

import wandledi.scala.example.controllers._

class Bootstrap extends WandlediBootstrap {

  def init(servletContext: ServletContext) {

    setPersistenceUnit("ScalaPU")
    addControllers(classOf[Home])
  }

  def destroy() {

  }
}
