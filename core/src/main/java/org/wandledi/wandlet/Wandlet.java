package org.wandledi.wandlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.wandledi.Resources;

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
public class Wandlet implements Resources {

    private ServletContext servletContext;
    private String charset = "UTF-8";
    private String contentType = "text/html";
    private boolean directFileAccess = false;
    private boolean debug = false;
    private boolean html5 = false;

    protected Wandlet() {

    }

    public Wandlet(ServletContext servletContext, boolean html5) {
        this.servletContext = servletContext;
        this.html5 = html5;
    }

    public Wandlet(ServletContext servletContext) {
        this(servletContext, false);
    }

    private String getContentTypeWithCharset() {
        return getContentType() + ";charset=" + getCharset();
    }

    /**Used to read an (X)HTML file from the hard drive.
     * Override this if the used XHTML files reside somewhere else
     * than the paths they are referred to with.
     * Such as when you want to refer to "WEB-INF/templates/user/index.xhtml" as
     * "user/index.xhtml" or something along those lines.
     *
     * The default implementation should look in the webapp directory (i.e. WEB-INF/..).
     */
    public Reader open(String file) throws IOException {
        return new InputStreamReader(inputStreamFor(file), getCharset());
    }

    protected InputStream inputStreamFor(String file) throws IOException {
        if (isDirectFileAccess()) {
            return new FileInputStream(getServletContext().getRealPath(file));
        } else {
            InputStream in = getServletContext().getResourceAsStream(file);
            if (in != null) {
                return in;
            } else {
                throw new FileNotFoundException("Could find not resource '" + file + "'");
            }
        }
    }

    /**Used to write to the HttpServletResponse.
     * Sets the content type before opening the response for writing.
     */
    protected Writer open(HttpServletResponse response) throws IOException {
        response.setContentType(getContentTypeWithCharset());
        return response.getWriter();
    }

    /**The Wandler used to transform the input page.
     * Remember that Wandlers are stateful and therefore a single instance
     * must not be used by more than one Thread at the same time.
     */
    protected Wandler getWandler() {
        Wandler wandler = html5 ? Wandler.forHTML() : Wandler.forXHTML();
        wandler.setResources(this);
        return wandler;
    }

    /**Writes the transformed input page to the HttpServletResponse.
     *
     * @param response The response to be rendered.
     * @param httpServletResponse Render it using this HttpServletResponse.
     */
    public void render(Response response, HttpServletResponse httpServletResponse) throws IOException {
        long ms = System.currentTimeMillis();
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
            if (debug) {
                ms = System.currentTimeMillis() - ms;
                Logger.getLogger("org.wandledi").info("Rendered " + response +
                    " within " + ms + " ms.");
            }
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    /** Sets the charset to use. */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /** Charset to use. Default: UTF-8 */
    public String getCharset() {
        return charset;
    }

    /** Sets the content type to use (without charset). */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /** The content type to use. Default: text/html */
    public String getContentType() {
        return contentType;
    }

    public void setDirectFileAccess(boolean directFileAccess) {
        this.directFileAccess = directFileAccess;
    }

    /**Indicates whether files to load should be accessed directly
     * by Wandlet using a FileInputStream or if it should
     * be accessed through the ServletContext.
     *
     * @return True if direct file access is enabled.
     */
    public boolean isDirectFileAccess() {
        return directFileAccess;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setHtml5(boolean html5) {
        this.html5 = html5;
    }

    public boolean isHtml5() {
        return html5;
    }
}
