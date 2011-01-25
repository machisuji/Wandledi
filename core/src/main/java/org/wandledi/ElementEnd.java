package org.wandledi;

import org.xml.sax.Attributes;

/**
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

    public final boolean isStart() {
        return false;
    }

    public final boolean isEnd() {
        return true;
    }

    public final boolean isCharacters() {
        return false;
    }

    public final Attributes getAttributes() {
        return null;
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
