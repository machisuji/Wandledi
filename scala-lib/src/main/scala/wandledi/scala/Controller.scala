package wandledi.scala

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import wandledi.java.Switchboard
import wandledi.java.WandlediRequest

@serializable
class Controller extends wandledi.java.WandlediController with Persistence {

  @transient protected var request: HttpServletRequest = _
  @transient protected var response: HttpServletResponse = _
  @transient protected var flash: Flash = _
  @transient protected var session: SessionData = _
  @transient protected var parameters: Parameters = _
  @transient protected var model: RequestData = _
  @transient protected var wandlediRequest: WandlediRequest = new WandlediRequest

  object JavaBase extends wandledi.java.Controller
  lazy val javaHelper = {
    val controller = new wandledi.java.Controller
    controller.init(request, response)
    controller
  }
  def getName(): String = JavaBase.getName(getClass)

  def init(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) {
    request = httpRequest
    response = httpResponse
    flash = new Flash(Switchboard.getFlash(request))
    parameters = new Parameters(request)
    session = new SessionData(request.getSession)
    model = new RequestData(request)
  }
  def getWandlediRequest = wandlediRequest

  def isSpellController = false
  def controllerRegistered() { }
  def beforeAction() { }
  def afterAction() { }

  def database = javaHelper.getDatabase
  def encode(str: String) = wandledi.java.Controller.encode(str)
  def decode(str: String) = wandledi.java.Controller.decode(str)

  def servletContext = Switchboard.getInstance.getServletContext
  def messages = Switchboard.getInstance.getMessages(request)

  def params = parameters

  /**In a Scala Controller write this.<method> to use any method of the
   * underlying Java Controller.
   */
  implicit def toJavaController(ctrl: Controller): wandledi.java.Controller = javaHelper
}
