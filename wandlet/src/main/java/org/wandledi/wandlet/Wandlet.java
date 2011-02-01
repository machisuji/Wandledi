package org.wandledi.wandlet;

import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import javax.servlet.ServletContext;

import org.wandledi.Wandler;

/**A Wandlet enables output of XHTML file transformed through Wandledi.
 * It's thread-safe, so just use an instance where ever you like.
 * To give a little example:
 *
 * <pre>public class MyServlet extends HttpServlet {
 *     private Wandlet wandlet = new Wandlet();
 *
 *     public void doGet(HttpServletRequest request, HttpServletResponse response) {
 *         boolean loggedIn = request.getSession().getAttribute("user") != null;
 *         wandlet.render(new Index(loggedIn), response);
 *     }
 * }
 *
 * class Index extends Page("index.xhtml") {
 *     public Index(boolean loggedIn) {
 *         if (loggedIn) {
 *             get("#msg").insert("Welcome back!");
 *         }
 *     }
 * }</pre>
 */
public class Wandlet {

    private ServletContext servletContext;

    protected Wandlet() {

    }

    public Wandlet(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    
    /**Used to read an XHTML file from the hard drive.
     * Override this if the used XHTML files reside somewhere else
     * than the paths they are referred to with.
     * Such as when you want to refer to "WEB-INF/templates/user/index.xhtml" as
     * "user/index.xhtml" or something along those lines.
     */
    protected Reader open(String file) throws IOException {
        return new InputStreamReader(servletContext.getResourceAsStream(file));
    }
    
    /**Used to write to the HttpServletResponse.
     */
    protected Writer open(HttpServletResponse response) throws IOException {
        return response.getWriter();
    }
    
    /**The Wandler used to transform the input page.
     * Remember that Wandlers are stateful and therefore a single instance
     * must not be used by more than one Thread at the same time.
     */
    protected Wandler getWandler() {
        return new Wandler();
    }

    /**Writes the transformed input page to the HttpServletResponse.
     *
     * @param response The response to be rendered.
     * @param httpServletResponse Render it using this HttpServletResponse.
     */
    public void render(Response response, HttpServletResponse httpServletResponse) throws IOException {
        Wandler wandler = getWandler();
        Reader input = null;
        Writer output = open(httpServletResponse);
        try {
            input = open(response.getFile());
            wandler.useScroll(response.getScroll());
            wandler.wandle(input, output);
        } finally {
            if (input != null) {
                try { input.close(); } catch (IOException dontCare) { }
            }       
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
