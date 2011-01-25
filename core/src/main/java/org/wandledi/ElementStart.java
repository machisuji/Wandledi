package org.wandledi;

import org.xml.sax.Attributes;

/**
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

    public final boolean isStart() {
        return true;
    }

    public final boolean isEnd() {
        return false;
    }

    public final boolean isCharacters() {
        return false;
    }

    public final Attributes getAttributes() {
        return attributes;
    }

    public final String getElement() {
        return name;
    }

    public final char[] getCharacters() {
        return null;
    }

    public final int getOffset() {
        return -1;
    }

    public final int getLength() {
        return -1;
    }
}
