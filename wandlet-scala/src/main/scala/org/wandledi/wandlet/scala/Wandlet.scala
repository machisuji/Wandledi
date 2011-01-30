package org.wandledi.wandlet.scala

import javax.servlet.http.HttpServletResponse
import org.wandledi.wandlet.Response

/**Mixin to render XHTML pages transformed through Wandledi.
 */
trait Wandlet {
  /**To be provided by classes which want to mix in scala.Wandlet.
   * The output is rendered using this HttpServletResponse.
   */
  def getHttpServletResponse: HttpServletResponse
  protected val javaWandlet = new org.wandledi.wandlet.Wandlet

  /**Renders the given response.
   *
   * @throws IOException
   */
  def render(response: Response) {
    javaWandlet.render(response, getHttpServletResponse)
  }
}

object Wandlet {
  /**Import this if you want to extend HttpServletResponse with
   * a method to render Wandledi Responses.
   */
  implicit def httpServletResponseToWandlet(response: HttpServletResponse) =
      new Wandlet {
        def getHttpServletResponse = response
      }
}
