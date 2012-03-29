package org.wandledi.wandlet.scala

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletResponse
import org.wandledi.wandlet.Response

/**
 * Mixin to render (X)HTML pages transformed through Wandledi.
 */
trait Wandlet {
  /**
   * To be provided by classes which want to mix in scala.Wandlet.
   * The output is rendered using this HttpServletResponse.
   */
  protected def httpServletResponse: HttpServletResponse

  /**
   * To be provided by classes which want to mix in scala.Wandlet.
   * Used for file resource (html file) lookup.
   */
  protected def servletContext: ServletContext

  protected val javaWandlet = new org.wandledi.wandlet.Wandlet {
    override def getServletContext = servletContext
  }

  /**
   * Renders the given response.
   *
   * @throws IOException
   */
  def render(response: Response) {
    javaWandlet.render(response, httpServletResponse)
  }

  object settings {
    def contentType_=(contentType: String) = javaWandlet.setContentType(contentType)

    def contentType = javaWandlet.getContentType

    def charset_=(charset: String) = javaWandlet.setCharset(charset)

    def charset = javaWandlet.getCharset

    def html5_=(html5: Boolean) = javaWandlet.setHtml5(html5)

    def html5 = javaWandlet.isHtml5

    def debug_=(debug: Boolean) = javaWandlet.setDebug(debug)

    def debug = javaWandlet.isDebug

    def directFileAccess_=(dfa: Boolean) = javaWandlet.setDirectFileAccess(dfa)

    def directFileAccess = javaWandlet.isDirectFileAccess
  }

}
