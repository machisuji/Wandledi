package org.wandledi;

/**Used to record a call of ContentHandler#endElement(...).
 *
 * @author Markus Kahl
 */
public class ElementEnd implements SpellLine {

    private String name;

    public ElementEnd(String name) {

        this.name = name;
    }

    public void perform(Spell parent) {

        parent.endElement(name);
    }
}
