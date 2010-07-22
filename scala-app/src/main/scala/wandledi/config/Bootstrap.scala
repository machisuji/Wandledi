package wandledi.config

import wandledi.java.WandlediBootstrap
import javax.servlet.ServletContext

import wandledi.scala.example.controllers._

class Bootstrap extends WandlediBootstrap {

  def init(servletContext: ServletContext) {

    wandledi.jetty.Application.setWebDirectory("src/main/webapp")

    setPersistenceUnit("MyPU")
    addControllers(classOf[Home])
  }

  def destroy() {

  }
}
