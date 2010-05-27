package wandledi.java.html;

import wandledi.core.Scroll;

/**An HTML page.
 *
 * @author Markus Kahl
 */
public class Pages {

    private Scroll scroll = new Scroll();
    private String file;

    public Pages() {

    }

    public void setFile(String file) {
        
        this.file = file;
    }

    public Scroll getScroll() {

        return scroll;
    }
    
    public Element get(String selector) {

        return new ElementImpl(selector, scroll);
    }
}
