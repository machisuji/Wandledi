package wandledi.java.html;

import wandledi.core.GrimoireSection;

/**An HTML page.
 *
 * @author Markus Kahl
 */
public class Pages {

    private GrimoireSection gs = new GrimoireSection();
    private String file;

    public Pages() {

    }

    public void setFile(String file) {
        
        this.file = file;
    }

    public GrimoireSection getGrimoireSection() {

        return gs;
    }
    
    public Element get(String selector) {

        return new ElementImpl(selector, gs);
    }
}
