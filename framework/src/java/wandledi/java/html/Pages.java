package wandledi.java.html;

import wandledi.core.*;

/**An HTML page.
 *
 * @author Markus Kahl
 */
public class Pages extends SelectableImpl {

    private String file;

    public Pages() {

        super(new Scroll());
    }

    public void setFile(String file) {
        
        this.file = file;
    }
}
