package org.wandledi.wandlet.scala

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse
import org.wandledi.wandlet.Response

/**Mixin to render XHTML pages transformed through Wandledi.
 */
trait Wandlet {
  /**To be provided by classes which want to mix in scala.Wandlet.
   * The output is rendered using this HttpServletResponse.
   */
  def httpServletResponse: HttpServletResponse
  def servletContext: ServletContext

  protected val javaWandlet = new org.wandledi.wandlet.Wandlet(servletContext)

  /**Renders the given response.
   *
   * @throws IOException
   */
  def render(response: Response) {
    javaWandlet.render(response, httpServletResponse)
  }
}
