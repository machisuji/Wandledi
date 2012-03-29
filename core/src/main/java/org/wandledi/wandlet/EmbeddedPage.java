package org.wandledi.wandlet;

import org.wandledi.CssSelector;
import org.wandledi.Scroll;
import org.wandledi.Selector;

/**A page that is embedded into a another one.
 * This is just for convenience.
 *
 * @author Markus Kahl
 */
public class EmbeddedPage extends Page {

    private Page gantry;
    private String file;
    private Selector embedAt;

    public EmbeddedPage(String file, String hostFile, Selector embedAt) {
        super(null);
        this.file = file;
        this.embedAt = embedAt;

        gantry = new Page(hostFile);
        gantry.get(embedAt).includeFile(file, scroll);
    }

    public EmbeddedPage(String file, String hostFile, String embedAt) {
        this(file, hostFile, CssSelector.valueOf(embedAt));
    }

    @Override
    public String getFile() {
        return gantry.getFile();
    }

    @Override
    public Scroll getScroll() {
        return gantry.getScroll();
    }

    @Override
    public String toString() {
        return "EmbeddedPage(" + file + " at " + embedAt + " in " + gantry.getFile() + ")";
    }
}
