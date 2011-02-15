package org.wandledi;

import org.xml.sax.Attributes;

/**Used to record a call of ContentHandler#startElement(...).
 *
 * @author Markus Kahl
 */
public class ElementStart implements SpellLine {

    protected String name;
    protected Attributes attributes;

    public ElementStart(String name, Attribute... attributes) {

        this(name, new SimpleAttributes(attributes));
    }

    public ElementStart(String name, Attributes attributes) {

        this.name = name;
        this.attributes = attributes;
    }

    public void perform(Spell parent) {

        parent.startElement(name, attributes);
    }
}
