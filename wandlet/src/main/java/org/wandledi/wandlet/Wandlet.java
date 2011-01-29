package org.wandledi.wandlet;

import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.wandledi.Wandler;
import org.wandledi.Scroll;

/**A Wandlet enables output of XHTML file transformed through Wandledi.
 */
public abstract class Wandlet {
    
    public abstract HttpServletResponse getHttpServletResponse();
    
    /**Used to read an XHTML file from the hard drive.
     * Override this if the used XHTML files reside somewhere else
     * than the paths they are referred to with.
     * Such as when you want to refer to "WEB-INF/templates/user/index.xhtml" as
     * "user/index.xhtml" or something along those lines.
     */
    protected Reader open(String file) throws IOException {
        return new FileReader(file);
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
     */
    public void render(Response response) throws IOException {
        Wandler wandler = getWandler();
        Reader input = null;
        Writer output = open(getHttpServletResponse());
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
}
